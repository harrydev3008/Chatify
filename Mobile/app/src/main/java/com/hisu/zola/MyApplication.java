package com.hisu.zola;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.hisu.zola.util.local.LocalDataManager;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initEmojiManager();
        createNotificationChannel();
        LocalDataManager.init(getApplicationContext());
    }

    @SuppressLint("ObsoleteSdkInt")
    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(getString(R.string.system_noty_channel_id),
                    getString(R.string.system_noty_channel_name), importance);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void initEmojiManager() {
        EmojiManager.install(new GoogleEmojiProvider());
    }
}