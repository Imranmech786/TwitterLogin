package com.imran.fyndtestapp.application;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;
import com.imran.fyndtestapp.BuildConfig;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                BuildConfig.TWITTER_API_KEY,
                BuildConfig.TWITTER_SECRET_KEY_API_KEY);

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();

        Twitter.initialize(twitterConfig);
        FirebaseApp.initializeApp(this);
        Fresco.initialize(this);
    }
}
