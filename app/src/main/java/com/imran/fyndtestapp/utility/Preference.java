package com.imran.fyndtestapp.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class Preference {
    private Context mContext;
    private String mPrefName;

    public Preference(Context applicationContext, String preferenceName) {
        mContext = applicationContext;
        mPrefName = preferenceName;
    }

    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(mPrefName, Context.MODE_PRIVATE);
    }

    public SharedPreferences.Editor edit() {
        return getSharedPreferences().edit();
    }

    public void putString(String key, String value) {
        edit().putString(key, value).commit();
    }


    public void putLong(String key, long value) {
        edit().putLong(key, value).commit();
    }


    public long getLong(String key, long defaultValue) {
        SharedPreferences globalPreference = mContext.getSharedPreferences(mPrefName, Context.MODE_MULTI_PROCESS);
        return globalPreference.getLong(key, defaultValue);
    }

    public String getString(String key, String defaultValue) {
        SharedPreferences globalPreference = mContext.getSharedPreferences(mPrefName, Context.MODE_MULTI_PROCESS);
        return globalPreference.getString(key, defaultValue);
    }

    public void clear() {
        edit().clear().commit();
    }
}
