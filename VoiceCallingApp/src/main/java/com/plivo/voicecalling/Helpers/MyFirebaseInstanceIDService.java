package com.plivo.voicecalling.Helpers;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.plivo.voicecalling.Activities.MainActivity;

/**
 * Created by Siva on 20/06/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private MainActivity mainActivity;

    public void setMainActivity(MainActivity instance){
        mainActivity = instance;
    }
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        // sendRegistrationToServer(refreshedToken);
        sendMessageToActivity(refreshedToken);

        Toast.makeText(MyFirebaseInstanceIDService.this,refreshedToken, Toast.LENGTH_LONG).show();

    }

    private void sendMessageToActivity(String msg) {
        Intent intent = new Intent("intentKey");
        intent.putExtra("key", msg);
        LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(intent);
    }

}
