package com.plivo.plivoaddressbook.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.plivo.plivoaddressbook.App;
import com.plivo.plivoaddressbook.layer.plivo.PlivoBackend;

import javax.inject.Inject;

public class PlivoFCMService extends FirebaseMessagingService {
    private static final String TAG = PlivoFCMService.class.getSimpleName();

    @Inject
    PlivoBackend backend;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        handleNotification(remoteMessage.getData().toString());
        backend.relayPushNotification(remoteMessage.getData());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((App) getApplication()).getAppComponent().inject(this);
    }

    private void handleNotification(String message) {
        Log.d(TAG, "push message: " + message);
    }
}
