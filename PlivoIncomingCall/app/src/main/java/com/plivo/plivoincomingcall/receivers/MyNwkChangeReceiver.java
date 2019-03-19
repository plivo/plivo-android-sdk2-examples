package com.plivo.plivoincomingcall.receivers;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.plivo.endpoint.NetworkChangeReceiver;

public class MyNwkChangeReceiver extends NetworkChangeReceiver {
    private boolean isRegistered;

    public void register(Context context) {
        if (!isRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(this, intentFilter);
        }
        isRegistered = true;
    }

    public void unregister(Context context) {
        if (isRegistered) {
            context.unregisterReceiver(this);
        }
        isRegistered = false;
    }
}
