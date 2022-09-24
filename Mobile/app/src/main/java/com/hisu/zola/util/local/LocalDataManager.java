package com.hisu.zola.util.local;

import android.content.Context;

import com.google.gson.Gson;
import com.hisu.zola.entity.User;

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

    public static void setCurrentUserInfo(User user) {
        getInstance().mySharedPreferences.putString(
                MySharedPreferences.USER_INFO_KEY, new Gson().toJson(user)
        );
    }

    public static User getCurrentUserInfo() {
        return new Gson().fromJson(
                getInstance().mySharedPreferences.getString(MySharedPreferences.USER_INFO_KEY),
                User.class
        );
    }
}