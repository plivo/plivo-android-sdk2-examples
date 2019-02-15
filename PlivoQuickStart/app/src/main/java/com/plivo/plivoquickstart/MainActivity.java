package com.plivo.plivoquickstart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.EventListener;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements EventListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Endpoint endpoint;
    private Outgoing outgoing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((ToggleButton) findViewById(R.id.call_btn))
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            call();
                        } else {
                            endCall();
                        }
//                        test();
                    }
                });
        endpoint = Endpoint.newInstance(true, this);
        login();

    }

    private void test() {
        Map<String, String> ok = new HashMap<String, String>();
        ok.put("label", "xxxx");
        ok.put("index", "xxxx");
//        endpoint.relayVoipPushNotification(ok);

//        outgoing = endpoint.createOutgoingCall();
//        if (outgoing != null) {
//            outgoing.callH(Inputs.TEST_CALL_PSTN, ok);
//        }


    }

    private void call() {
        outgoing = endpoint.createOutgoingCall();
        if (outgoing != null) {
            outgoing.call(Inputs.TEST_CALL_ENDPOINT);
//            outgoing.call(Inputs.TEST_CALL_PSTN);
        }
    }

    private void endCall() {
        if (outgoing != null) {
            outgoing.hangup();
        }
    }

    private void login() {
        if (!endpoint.getRegistered()) {
            endpoint.login(Inputs.TEST_USERNAME, Inputs.TEST_PASSWORD);
        }
    }

    @Override
    public void onLogin() {
        Log.d(TAG, "onLogin");
    }

    @Override
    public void onLogout() {
        Log.d(TAG, "onLogout");
    }

    @Override
    public void onLoginFailed() {
        Log.d(TAG, "onLoginFailed");
    }

    @Override
    public void onIncomingDigitNotification(String s) {
        Log.d(TAG, "onIncomingDigitNotification");
    }

    @Override
    public void onIncomingCall(Incoming incoming) {
        Log.d(TAG, "onIncomingCall");
    }

    @Override
    public void onIncomingCallHangup(Incoming incoming) {
        Log.d(TAG, "onIncomingCallHangup");
    }

    @Override
    public void onIncomingCallRejected(Incoming incoming) {
        Log.d(TAG, "onIncomingCallRejected");
    }

    @Override
    public void onOutgoingCall(Outgoing outgoing) {
        this.outgoing = outgoing;
        Log.d(TAG, "onOutgoingCall");
    }

    @Override
    public void onOutgoingCallAnswered(Outgoing outgoing) {
        this.outgoing = outgoing;
        Log.d(TAG, "onOutgoingCallAnswered");
    }

    @Override
    public void onOutgoingCallRejected(Outgoing outgoing) {
        this.outgoing = outgoing;
        Log.d(TAG, "onOutgoingCallRejected");
    }

    @Override
    public void onOutgoingCallHangup(Outgoing outgoing) {
        this.outgoing = outgoing;
        Log.d(TAG, "onOutgoingCallHangup");
    }

    @Override
    public void onOutgoingCallInvalid(Outgoing outgoing) {
        this.outgoing = outgoing;
        Log.d(TAG, "onOutgoingCallInvalid");
    }
}
