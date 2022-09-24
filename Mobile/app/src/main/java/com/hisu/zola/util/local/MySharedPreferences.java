package com.hisu.zola.util.local;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    public static final String SHARED_PREFERENCE = "ZOLA_SHARED_PREFERENCE";
    public static final String USER_LOGIN_KEY = "USER_LOGIN_STATE";


    private Context mContext;

    public MySharedPreferences(Context mContext) {
        this.mContext = mContext;
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences sharedPreferences = getSharedPreferences();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        return getSharedPreferences().getBoolean(key, false);
    }

    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(
                SHARED_PREFERENCE, Context.MODE_PRIVATE
        );
    }
}