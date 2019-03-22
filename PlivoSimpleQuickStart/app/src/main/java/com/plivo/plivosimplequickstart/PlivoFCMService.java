package com.plivo.plivosimplequickstart;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import static com.plivo.plivosimplequickstart.Utils.INCOMING_CALL_DATA;

public class PlivoFCMService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData() != null) {
            HashMap<String, String> map = new HashMap<>();
            for (Map.Entry<String, String> entry: remoteMessage.getData().entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
            ((App) getApplication()).plivoEndpoint.relayVoipPushNotification(map);
            relayPush(map);
        }
    }

    private void relayPush(HashMap<String, String> incomingData) {
        startActivity(new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(INCOMING_CALL_DATA, incomingData)
        );
    }
}
