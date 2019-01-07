package com.plivo.plivoaddressbook.services;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.plivo.plivoaddressbook.App;
import com.plivo.plivoaddressbook.layer.plivo.PlivoBackend;

import javax.inject.Inject;

public class PlivoFCMService extends FirebaseMessagingService {
    @Inject
    PlivoBackend backend;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        backend.relayPushNotification(remoteMessage.getData());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((App) getApplication()).getAppComponent().inject(this);
    }
}
