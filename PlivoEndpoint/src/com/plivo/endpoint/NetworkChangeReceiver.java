package com.plivo.endpoint;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.plivo.endpoint.backend.plivo;
import com.plivo.endpoint.Endpoint.*;

public class NetworkChangeReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        try
        {
            if (isOnline(context)) {
                ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
                ActivityManager.getMyMemoryState(myProcess);
                System.out.println("internet available");
                System.out.println(myProcess.importance);

                boolean isOpen = (myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_GONE);
                if (isOpen) {
                    plivo.LoginAgain(true);
                    System.out.println("app is open ");
                } else {
                    System.out.println("app is not open ");
                }
            } else {
                //plivo.LoginAgain(0);
            }
        } catch (NullPointerException e) {
                                System.out.println("exception... ");
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        System.out.println("app is checking for internet connection status...");
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}