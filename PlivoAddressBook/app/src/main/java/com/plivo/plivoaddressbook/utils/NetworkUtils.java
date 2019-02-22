package com.plivo.plivoaddressbook.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.core.net.ConnectivityManagerCompat;

public class NetworkUtils {
    private Context context;

    public NetworkUtils(Context context) {
        this.context = context;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nwkInf = cm.getActiveNetworkInfo();
        return nwkInf != null && nwkInf.isConnected();
    }

}
