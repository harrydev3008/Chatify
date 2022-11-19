package com.hisu.zola.util.network;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class NetworkConnectionModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> networkAvailable = new MutableLiveData<>();

    public NetworkConnectionModel(@NonNull Application application) {
        super(application);

        ConnectivityManager manager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (manager != null) {
            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)//mobile data
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)//wifi
                    .build();

            manager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
                public void onAvailable(@NonNull Network network) {networkAvailable.postValue(true);}

                public void onLost(@NonNull Network network) {
                    networkAvailable.postValue(false);
                }

                public void onUnavailable() {
                    networkAvailable.postValue(false);
                }
            });
        } else {
            networkAvailable.setValue(true);
        }
    }

    public MutableLiveData<Boolean> getNetworkInfo() {
        return networkAvailable;
    }
}