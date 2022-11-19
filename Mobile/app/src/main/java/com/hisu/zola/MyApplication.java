package com.hisu.zola;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.StrictMode;

import com.hisu.zola.util.local.LocalDataManager;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        LocalDataManager.init(getApplicationContext());

        //Enable strict mode to prevent exception: exposed beyond app through intent.getdata()
        //Not recommend this way but ... yeah xD
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    @SuppressLint("ObsoleteSdkInt")
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(getString(R.string.system_noty_channel_id),
                    getString(R.string.system_noty_channel_name), importance);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}