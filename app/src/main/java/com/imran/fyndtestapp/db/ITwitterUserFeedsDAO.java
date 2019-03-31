package com.imran.fyndtestapp.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.imran.fyndtestapp.model.TwitterTweet;

import java.util.List;

@Dao
public interface ITwitterUserFeedsDAO {

    @Query("SELECT * FROM twitter_feeds")
    List<TwitterTweet> loadAllUserFeeds();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveUserFeed(List<TwitterTweet> movie);

    @Query("DELETE FROM twitter_feeds")
    void removeUserFeed();

}
