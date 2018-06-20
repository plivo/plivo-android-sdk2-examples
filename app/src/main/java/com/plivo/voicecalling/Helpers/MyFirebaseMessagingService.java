package com.plivo.voicecalling.Helpers;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;

import java.util.Map;

/**
 * Created by Siva on 19/06/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService implements EndPointListner {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getData() != null)
        {
            Map<String, String> msgData = remoteMessage.getData();

            Log.d("Token Data title:" + remoteMessage.getData().get("title"),"Message Data Body "+remoteMessage.getData());
            Phone.getInstance(this).endpoint.relayVoipPushNotification(msgData);
        }

    }

    public void onLogin() {

        Log.d("PlivoInbound", "Logging in");

    }

    public void onLogout() {

        Log.d("PlivoInbound", "Logged out");

    }

    public void onLoginFailed() {

        Log.d("PlivoInbound", "Login failed");

    }

    public void onIncomingCall(Incoming incoming) {

        Log.d("FCM", "Incoming call received");

    }

    public void onIncomingCallHangup(Incoming incoming) {

    }

    public void onIncomingCallRejected(Incoming incoming) {

    }

    public void onOutgoingCall(Outgoing outgoing) {

    }

    public void onOutgoingCallAnswered(Outgoing outgoing) {

    }

    public void onOutgoingCallRejected(Outgoing outgoing) {

    }

    public void onOutgoingCallHangup(Outgoing outgoing) {

    }

    public void onOutgoingCallInvalid(Outgoing outgoing){

    }

    public void onIncomingDigitNotification(String digits) {

    }
}
