package com.hisu.zola.util.local;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    public static final String SHARED_PREFERENCE = "ZOLA_SHARED_PREFERENCE";
    public static final String USER_LOGIN_KEY = "USER_LOGIN_STATE";
    public static final String USER_INFO_KEY = "USER_LOGIN_STATE";

    private final Context mContext;

    public MySharedPreferences(Context mContext) {
        this.mContext = mContext;
    }

    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(
                SHARED_PREFERENCE, Context.MODE_PRIVATE
        );
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        return getSharedPreferences().getBoolean(key, false);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return getSharedPreferences().getString(key, "");
    }
}