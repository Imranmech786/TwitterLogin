package com.imran.fyndtestapp.twitter;

import com.imran.fyndtestapp.model.TwitterTweet;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MyTwitterApiClient extends TwitterApiClient {
    public MyTwitterApiClient(TwitterSession session) {
        super(session);
    }

    public GetUsersShowAPICustomService getCustomService() {
        return getService(GetUsersShowAPICustomService.class);
    }
}

interface GetUsersShowAPICustomService {
    @GET("/1.1/statuses/user_timeline.json")
    Call<List<TwitterTweet>> getfeed(@Query("user_id") long name);
}
