package com.plivo.plivosimplequickstart;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

import static com.plivo.plivosimplequickstart.Utils.INCOMING_CALL_DATA;

public class PlivoFCMService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        relayPush((HashMap<String, String>) remoteMessage.getData());
    }

    private void relayPush(HashMap<String, String> data) {
        startActivity(new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(INCOMING_CALL_DATA, data)
        );
    }
}
