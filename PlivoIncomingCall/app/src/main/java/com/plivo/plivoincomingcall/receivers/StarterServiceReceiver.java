package com.plivo.plivoincomingcall.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.plivo.plivoincomingcall.App;
import com.plivo.plivoincomingcall.utils.Constants;

import static android.content.Intent.ACTION_BOOT_COMPLETED;

public class StarterServiceReceiver extends BroadcastReceiver {
    private static final String TAG = StarterServiceReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;
        Log.d(TAG, "onReceive " + intent.getAction());

        switch (intent.getAction()) {
            case ACTION_BOOT_COMPLETED:
            case Constants.ACTION_ALARM_RECEIVED:
//                ((App) context.getApplicationContext()).startBakgroundService();
                break;
        }
    }
}
