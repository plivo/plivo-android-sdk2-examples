package com.plivo.voicecalling.Activities;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;
import com.plivo.voicecalling.Helpers.AppHelper;
import com.plivo.voicecalling.Helpers.EndPointListner;
import com.plivo.voicecalling.Helpers.Phone;
import com.plivo.voicecalling.R;

import java.util.Locale;
import java.util.Timer;

public class ActiveCall extends AppCompatActivity implements EndPointListner {

    public Endpoint endpoint;
    public Outgoing outgoing;

    Timer timer;

    private Handler customHandler = new Handler();
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    private boolean backPressed = false;

    private Thread backPressedThread = null;

    private boolean isSpeakerOn = false;

    private boolean isCallInMuteState = false;

    TextView callTimer;

    TextView callTitle;

    TextView callerPhoneField;

    AppCompatImageView hangUpBtn;

    AppCompatImageView speakerBtn;

    AppCompatImageView micToggle;

    String phoneNumberStr;

    EditText dtmfText;

    Button dtmfBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        endpoint = Phone.getInstance(this).endpoint;

        outgoing = new Outgoing(endpoint);
        outgoing = endpoint.createOutgoingCall();

        callTitle = (TextView) findViewById(R.id.call_title);
        callerPhoneField = (TextView) findViewById(R.id.caller_phone);
        callTimer =  (TextView) findViewById(R.id.call_timer);
        speakerBtn = (AppCompatImageView) findViewById(R.id.speaker);
        micToggle = (AppCompatImageView) findViewById(R.id.mic_toggle);
        hangUpBtn = (AppCompatImageView) findViewById(R.id.hang_up);

        dtmfText = (EditText) findViewById(R.id.editText2);
        dtmfBtn = (Button) findViewById(R.id.dtmfBtn);

        initializerView();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            phoneNumberStr = extras.getString("data");
            callerPhoneField.setText(phoneNumberStr);

            outgoing.call(phoneNumberStr);

        } else {
            // handle case

        }
    }

    /**
     * Handle onDestroy event which is implement by RtcListener class
     * Destroy the video source
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        stopTimer();

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

    private void initializerView() {

        timer = new Timer();

        hangUpBtn.setOnClickListener(v -> {
            hangUp();
        });


        micToggle.setOnClickListener(v -> {

            if(!isCallInMuteState)
            {
                isCallInMuteState = true;

                outgoing.mute();
                Toast.makeText(getApplicationContext(), "Muted", Toast.LENGTH_LONG).show();

            }
            else
            {
                isCallInMuteState = false;

                outgoing.unmute();
                Toast.makeText(getApplicationContext(), "Unmuted", Toast.LENGTH_LONG).show();

            }
        });


        speakerBtn.setOnClickListener(v -> {

            AudioManager mAudioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            if(!isSpeakerOn)
            {
                isSpeakerOn = true;

                mAudioMgr.setSpeakerphoneOn(true);
                mAudioMgr.setMode(AudioManager.MODE_IN_COMMUNICATION);
                Toast.makeText(getApplicationContext(), "SpeakerPhone On", Toast.LENGTH_LONG).show();
            }
            else
            {
                isSpeakerOn = false;

                mAudioMgr.setSpeakerphoneOn(false);
                mAudioMgr.setMode(AudioManager.MODE_IN_COMMUNICATION);
                Toast.makeText(getApplicationContext(), "SpeakerPhone Off", Toast.LENGTH_LONG).show();
            }

        });

    }

    /**
     * Handle when people click hangUp button
     * Destroy all video resources and connection
     */
    public void hangUp() {
        stopTimer();//// TODO: 12/3/16 make time blink animation
        try {
            this.endCall();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        finish();
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

    @Override
    public void onBackPressed() {
        if (!this.backPressed) {
            this.backPressed = true;
            AppHelper.CustomToast(this, "Press again to end the ActiveCall.");
            this.backPressedThread = new Thread(() -> {
                try {
                    hangUp();
                    Thread.sleep(5000);
                    backPressed = false;
                } catch (InterruptedException e) {
                    Log.d("Call Activity"," Successfully interrupted");
                }
            });
            this.backPressedThread.start();
            return;
        }
        if (this.backPressedThread != null)
            this.backPressedThread.interrupt();
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void sendDtmf(View view) {

        if (dtmfText.getText().toString().equals("")) {

            Toast.makeText(this, "InValid DTMF", Toast.LENGTH_SHORT).show();

        }else{

//            if (incoming != null) { SUP 113
//
//                incoming.sendDigits(dtmfText.getText().toString());
//            } else {
                outgoing.sendDigits(dtmfText.getText().toString());

//            }

            dtmfText.setText("");

        }
    }

    public void onLogin() {
        Log.d("PlivoInbound", "Logged In");
    }

    public void onLogout() {
        Log.d("PlivoInbound", "Logged out");
    }

    public void onLoginFailed() {
        Log.d("PlivoInbound", "Login failed");
    }

    public void onIncomingCall(Incoming incoming) {

    }

    public void onIncomingCallHangup(Incoming incoming) {
        Log.d("PlivoInbound", "Call hanging up");

    }

    public void onIncomingCallRejected(Incoming incoming) {

        Log.d("onIncomingCall", "Rejected");

    }

    public void onOutgoingCall(Outgoing outgoing) {

        Log.d("onOutgoingCall", "onOutgoingCall");
    }

    public void onOutgoingCallAnswered(Outgoing outgoing) {
        Log.d("onOutgoingCall", "Answered");

        setTimer();

    }

    public void onOutgoingCallRejected(Outgoing outgoing) {
        Log.d("onOutgoingCall", "Rejected");

        this.endCall();
    }

    public void onOutgoingCallHangup(Outgoing outgoing) {
        Log.d("onOutgoingCall", "Hangup");

        this.endCall();
    }

    public void onOutgoingCallInvalid(Outgoing outgoing) {
        Log.d("onOutgoingCall", "Invalid");

        stopTimer();

        this.endCall();
    }

    public void onIncomingDigitNotification(String digits) {
        Log.d("onIncoming", "DigitNotification");

    }

    public void endCall() {

        AudioManager mAudioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioMgr.setSpeakerphoneOn(false);
        mAudioMgr.setMode(AudioManager.MODE_IN_COMMUNICATION);

        Log.d("Outgoing", "Hangup");
            outgoing.hangup();
    }
}
