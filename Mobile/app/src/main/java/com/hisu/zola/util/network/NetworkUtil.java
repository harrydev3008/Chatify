package com.hisu.zola.util.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    public static boolean isConnectionAvailable(Context context) {
        boolean isConnected = true;

        ConnectivityManager connectivityManager = context.getSystemService(ConnectivityManager.class);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null)
            isConnected = false;

        return isConnected;
    }
}