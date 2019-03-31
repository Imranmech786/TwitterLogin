package com.imran.fyndtestapp.twitter;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.gson.Gson;
import com.imran.fyndtestapp.R;
import com.imran.fyndtestapp.db.TwitterUserFeedDatabase;
import com.imran.fyndtestapp.model.TwitterTweet;
import com.imran.fyndtestapp.utility.Constants;
import com.imran.fyndtestapp.utility.Network;
import com.imran.fyndtestapp.utility.Preference;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {

    private static final String TAG = "MainAcvtivity";
    private FirebaseAuth mAuth;
    private TwitterLoginButton loginButton;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Preference preference;
    private TwitterUserFeedDatabase database;
    private TextView retry;
    private TwitterFeedsAdapter twitterFeedsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeWidgets();
        setUpActionBar();
        mAuth = FirebaseAuth.getInstance();
        preference = new Preference(getApplicationContext(), Constants.PREFERENCE_NAME);
        database = Room.databaseBuilder(getApplicationContext(), TwitterUserFeedDatabase.class, Constants.DATABASE_NAME)
                .fallbackToDestructiveMigration().build();
        TwitterLogin();
        long userId = preference.getLong(Constants.USER_ID, -1);
        if (userId != -1) {
            /*Fetch user feed*/
            retry.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            if (Network.isConnected(getApplicationContext())) {
                loadTwitterFeed();
            } else {
                getDataFromDb();
            }
        } else {
            /*Ask for User login*/
            if (Network.isConnected(getApplicationContext())) {
                retry.setVisibility(View.GONE);
            } else {
                retry.setVisibility(View.VISIBLE);
                retry.setText(getString(R.string.retry));
                Toast.makeText(this, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void TwitterLogin() {
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "twitterLogin:success" + result);
                progressBar.setVisibility(View.VISIBLE);
                long userID = result.data.getId();
                handleTwitterSession(result.data, userID);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.w(TAG, "twitterLogin:failure", exception);
                progressBar.setVisibility(View.GONE);
                loginButton.setText(getString(R.string.sign_in_failed));
            }
        });
    }


    private void initializeWidgets() {
        retry = findViewById(R.id.retry);
        progressBar = findViewById(R.id.progressBar);
        loginButton = findViewById(R.id.login);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result to the Twitter login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    private void handleTwitterSession(TwitterSession session, long userID) {
        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        preference.putLong(Constants.USER_ID, userID);
                        String userSesion = new Gson().toJson(session);
                        preference.putString(Constants.TWITTER_SESSION, userSesion);
                        loadTwitterFeed();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        loginButton.setText(getString(R.string.sign_in_failed));
                    }
                });
    }

    private void loadTwitterFeed() {
        loginButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        retry.setVisibility(View.GONE);
        String bundleString = preference.getString(Constants.TWITTER_SESSION, null);
        TwitterSession twitterSession = new Gson().fromJson(bundleString, TwitterSession.class);
        long userId = preference.getLong(Constants.USER_ID, 0);
        new MyTwitterApiClient(twitterSession).getCustomService().getfeed(userId)
                .enqueue(new Callback<List<TwitterTweet>>() {
                    @Override
                    public void success(Result<List<TwitterTweet>> result) {
                        invalidateOptionsMenu();
                        progressBar.setVisibility(View.GONE);
                        if (result.data != null && !result.data.isEmpty()) {
                            Executors.newSingleThreadExecutor().execute(() -> {
                                database.movieDAO().removeUserFeed();
                                database.movieDAO().saveUserFeed(result.data);
                                List<TwitterTweet> tweetList = database.movieDAO().loadAllUserFeeds();
                                runOnUiThread(() -> setAdapter(tweetList));
                            });
                        } else {
                            loginButton.setVisibility(View.GONE);
                            retry.setVisibility(View.VISIBLE);
                            retry.setText(getString(R.string.no_feeds_found));
                        }
                        Log.d(TAG, "feeds" + result.toString());
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "Failed" + exception.toString());
                    }
                });
    }

    private void getDataFromDb() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<TwitterTweet> tweetList = database.movieDAO().loadAllUserFeeds();
            if (tweetList != null && !tweetList.isEmpty()) {
                runOnUiThread(() -> setAdapter(tweetList));
            } else {
                retry.setVisibility(View.VISIBLE);
                retry.setText(getString(R.string.no_feeds_found));
            }
        });
    }

    private void setAdapter(List<TwitterTweet> tweetList) {
        recyclerView.setVisibility(View.VISIBLE);
        if (twitterFeedsAdapter != null) {
            twitterFeedsAdapter.update(tweetList);
        } else {
            twitterFeedsAdapter = new TwitterFeedsAdapter(this, tweetList);
            recyclerView.setAdapter(twitterFeedsAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(getBaseContext());
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.log_out);
        menuItem.setOnMenuItemClickListener(this);
        if (preference.getLong(Constants.USER_ID, -1) != -1) {
            menuItem.setVisible(true);
        } else {
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.log_out) {
            removeAllUserData();
            item.setVisible(false);
        }
        return false;
    }

    private void removeAllUserData() {
        loginButton.setText(getString(R.string.login_with_twitter));
        mAuth.signOut();
        recyclerView.setVisibility(View.GONE);
        loginButton.setVisibility(View.VISIBLE);
        deleteDatabase();
        preference.clear();
    }

    private void deleteDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> database.movieDAO().removeUserFeed());
    }

    protected void setUpActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        ViewCompat.setElevation(toolbar, getResources().getDimension(R.dimen._3dp));
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_title));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
