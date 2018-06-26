package com.plivo.voicecalling.Activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;
import com.plivo.voicecalling.Helpers.EndPointListner;
import com.plivo.voicecalling.Helpers.Phone;
import com.plivo.voicecalling.R;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class IncomingCall extends AppCompatActivity implements EndPointListner{

    public Endpoint endpoint;

    public Incoming incomingCall;

    TextView callerPhoneField;

    AppCompatImageButton sliderAccept;

    AppCompatImageButton sliderReject;

    PulsatorLayout rejectPulsator;

    PulsatorLayout acceptPulsator;

    Timer timer;

    private Vibrator vibrator;

    long autoRejectDelay = 15000;

    private Handler customHandler = new Handler();
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    TextView callerName;
    TextView callTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        endpoint = Phone.getInstance(this).endpoint;

        sliderAccept = (AppCompatImageButton) findViewById(R.id.accept_call);
        sliderReject = (AppCompatImageButton) findViewById(R.id.reject_call);

        callTimer =  (TextView) findViewById(R.id.call_timer);
        callerName = (TextView) findViewById(R.id.caller_phone);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] vibrate = new long[]{2000, 2000, 2000, 2000, 2000};
        vibrator.vibrate(vibrate, 1);

        timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                rejectCall();
            }

        }, autoRejectDelay);


        sliderReject.setOnClickListener(view -> rejectCall());

        sliderAccept.setOnClickListener(view -> {
            acceptCall();
        });

        startPulser(acceptPulsator);
        startPulser(rejectPulsator);

//        AnimationsUtil.ShakeAnimation(this, findViewById(R.id.accept_call));
//        AnimationsUtil.ShakeAnimation(this, findViewById(R.id.reject_call));
    }

    /**
     * method to accept incoming calls
     */
    public void acceptCall() {

        sliderAccept.setVisibility(View.GONE);

        if (vibrator != null)
            vibrator.cancel();

        incomingCall.answer();

        setTimer();

    }

    /**
     * Publish a hangUp command if rejecting call.
     *
     */
    public void rejectCall() {
        if (vibrator != null)
            vibrator.cancel();

        incomingCall.hangup();

        stopTimer();

        finish();
    }

    private void startPulser(PulsatorLayout pulsatorLayout) {
        pulsatorLayout.start();
    }

    public void onLogin() {
        Log.d("PlivoInbound", "Logged In");

        runOnUiThread(new Runnable() {
            public void run() {
            }
        });
    }

    public void onLogout() {
        Log.d("PlivoInbound", "Logged out");

    }

    public void onLoginFailed() {

        Log.d("PlivoInbound", "Login failed");

    }

    public void onIncomingCall(Incoming incoming) {

        Log.d("Incoming call class", "incoming");

        incomingCall = incoming;

        callerName.setText(incomingCall.getFromContact());

    }

    public void onIncomingCallHangup(Incoming incoming) {
        Log.d("PlivoInbound", "Call hanging up");

        runOnUiThread(new Runnable() {
            public void run() {
            }
        });

    }

    public void onIncomingCallRejected(Incoming incoming) {

        Log.d("onIncomingCall", "Rejected");

        this.endCall();

    }

    public void onOutgoingCall(Outgoing outgoing) {

        Log.d("onOutgoingCall", "onOutgoingCall");

    }

    public void onOutgoingCallAnswered(Outgoing outgoing) {
        Log.d("onOutgoingCall", "Answered");

    }

    public void onOutgoingCallRejected(Outgoing outgoing) {
        Log.d("onOutgoingCall", "Rejected");
    }

    public void onOutgoingCallHangup(Outgoing outgoing) {
        Log.d("onOutgoingCall", "Hangup");

    }

    public void onOutgoingCallInvalid(Outgoing outgoing) {
        Log.d("onOutgoingCall", "Invalid");

    }

    public void onIncomingDigitNotification(String digits) {
        Log.d("onIncoming", "DigitNotification");

        runOnUiThread(new Runnable() {

            public void run() {

            }
        });
    }

    public void endCall() {

        Log.d("Incoming", "Hangup");
        incomingCall.hangup();
    }

    private String getTimer() {

        return callTimer.getText().toString().trim();
    }

    private void setTimer() {
        runOnUiThread(() -> callTimer.setVisibility(View.VISIBLE));
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    private void stopTimer() {
        runOnUiThread(() -> callTimer.setVisibility(View.GONE));
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);

            int mins = secs / 60;

            secs = secs % 60;

            callTimer.setText("" + mins + ":" + String.format(Locale.getDefault(), "%02d", secs));
            customHandler.postDelayed(this, 0);

        }

    };
}
