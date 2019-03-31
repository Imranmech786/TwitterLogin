package com.imran.fyndtestapp.utility;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imran.fyndtestapp.model.TwitterUser;

import java.lang.reflect.Type;

public class Converters {

    @TypeConverter
    public static TwitterUser fromString(String value) {
        Type listType = new TypeToken<TwitterUser>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayLisr(TwitterUser list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

}

