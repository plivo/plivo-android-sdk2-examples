package com.plivo.plivoaddressbook.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PlivoFCMService extends FirebaseMessagingService {
    private static final String TAG = PlivoFCMService.class.getSimpleName();

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.i(TAG, "onNewToken: " + s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        handleNotification(remoteMessage.getNotification().getBody());
    }

    private void handleNotification(String message) {
        Log.d(TAG, "push message: " + message);
    }
}
