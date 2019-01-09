package com.plivo.endpoint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.plivo.endpoint.backend.plivo;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.D("NetworkChangeReceiver " + "Network changed");
        try {
            if (isOnline(context)) {
                plivo.LoginAgain();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            boolean isConnected = netInfo != null && netInfo.isConnected();
            Log.D("isOnline: " + isConnected);
            return isConnected;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}