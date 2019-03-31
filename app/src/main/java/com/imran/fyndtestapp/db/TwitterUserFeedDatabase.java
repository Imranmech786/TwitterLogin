package com.imran.fyndtestapp.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.imran.fyndtestapp.model.TwitterTweet;
import com.imran.fyndtestapp.utility.Converters;

@Database(entities = {TwitterTweet.class}, version = 3, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class TwitterUserFeedDatabase extends RoomDatabase {

    public abstract ITwitterUserFeedsDAO movieDAO();
}
