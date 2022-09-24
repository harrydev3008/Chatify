package com.hisu.zola.util.local;

import android.content.Context;

public class LocalDataManager {

    private static LocalDataManager instance;
    private MySharedPreferences mySharedPreferences;

    public static void init(Context context) {
        instance = new LocalDataManager();
        instance.mySharedPreferences = new MySharedPreferences(context);
    }

    private synchronized static LocalDataManager getInstance() {
        if (instance == null)
            instance = new LocalDataManager();
        return instance;
    }

    public static void setUserLoginState(boolean isLogin) {
        getInstance().mySharedPreferences.putBoolean(
                MySharedPreferences.USER_LOGIN_KEY, isLogin
        );
    }

    public static boolean getUserLoginState() {
        return getInstance().mySharedPreferences.getBoolean(
                MySharedPreferences.USER_LOGIN_KEY
        );
    }
}