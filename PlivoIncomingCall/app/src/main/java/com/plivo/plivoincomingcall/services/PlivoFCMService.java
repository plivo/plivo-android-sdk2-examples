package com.plivo.plivoincomingcall.services;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.plivo.plivoincomingcall.App;
import com.plivo.plivoincomingcall.layer.plivo.PlivoBackend;
import com.plivo.plivoincomingcall.screens.login.LoginActivity;
import com.plivo.plivoincomingcall.utils.Constants;
import com.plivo.plivoincomingcall.utils.PreferencesUtils;

import java.util.Map;

import javax.inject.Inject;

public class PlivoFCMService extends FirebaseMessagingService {
    @Inject
    PlivoBackend backend;

    @Inject
    PreferencesUtils preferencesUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (backend.login(preferencesUtils.getUser(), success -> relayPush(remoteMessage.getData()))) {
            relayPush(remoteMessage.getData());
        }
    }

    private void relayPush(Map<String, String> data) {
        startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Constants.INCOMING_CALL, true)
        );
        backend.relayPushNotification(data);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((App) getApplication()).getAppComponent().inject(this);
    }
}
