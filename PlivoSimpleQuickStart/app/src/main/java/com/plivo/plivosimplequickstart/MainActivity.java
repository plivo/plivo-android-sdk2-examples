package com.plivo.plivosimplequickstart;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.EventListener;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements EventListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Endpoint plivoEndpoint;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (plivoEndpoint == null) {
            plivoEndpoint = new Endpoint(true, this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (!plivoEndpoint.getRegistered()) {
        loginWithToken();
//        }
    }

    private void loginWithToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            Log.d(TAG, "fcm token " + newToken);
            plivoEndpoint.login(Inputs.TEST_USERNAME, "12345", newToken);
        });
    }

    private void showOutCallUI() {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.outgoing)
                .setView(R.layout.dialog_outgoing_content_view)
                .setCancelable(true)
                .setNeutralButton(R.string.call, (dialog, which) -> {
                    // make call here

                })
                .show();
    }

    public void onClickBtnMakeCall(View view) {
        showOutCallUI();
    }

    @Override
    public void onLogin() {
//        showOutCallUI();
    }

    @Override
    public void onLogout() {

    }

    @Override
    public void onLoginFailed() {

    }

    @Override
    public void onIncomingDigitNotification(String s) {

    }

    @Override
    public void onIncomingCall(Incoming incoming) {

    }

    @Override
    public void onIncomingCallHangup(Incoming incoming) {

    }

    @Override
    public void onIncomingCallRejected(Incoming incoming) {

    }

    @Override
    public void onOutgoingCall(Outgoing outgoing) {

    }

    @Override
    public void onOutgoingCallAnswered(Outgoing outgoing) {

    }

    @Override
    public void onOutgoingCallRejected(Outgoing outgoing) {

    }

    @Override
    public void onOutgoingCallHangup(Outgoing outgoing) {

    }

    @Override
    public void onOutgoingCallInvalid(Outgoing outgoing) {

    }

}
