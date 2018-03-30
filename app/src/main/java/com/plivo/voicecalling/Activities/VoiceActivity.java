package com.plivo.voicecalling.Activities;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;
import com.plivo.voicecalling.Helpers.EndPointListner;
import com.plivo.voicecalling.Helpers.KeepAliveService;
import com.plivo.voicecalling.Helpers.Phone;
import com.plivo.voicecalling.R;

public class VoiceActivity extends AppCompatActivity implements EndPointListner {

    public static final String ACTION_INCOMING_CALL = "ACTION_INCOMING_CALL";

    public Endpoint endpoint;
    public Incoming incoming;
    public Outgoing outgoing;

    EditText dtmfText;
    Button answerBtn, declineBtn, muteBtn, holdBtn, dtmfBtn, logoutBtn;

    EditText phoneNumberText;
    Button callBtn;

    private VoiceBroadcastReceiver voiceBroadcastReceiver;

    private AlertDialog alertDialog;
    private NotificationManager notificationManager;

    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        endpoint = Phone.getInstance(this).endpoint;
        incoming = Phone.getInstance(this).incoming;
        outgoing = new Outgoing(endpoint);

        answerBtn = (Button) findViewById(R.id.answerBtn);
        declineBtn = (Button) findViewById(R.id.declineBtn);
        muteBtn = (Button) findViewById(R.id.muteBtn);

        dtmfText = (EditText) findViewById(R.id.editText2);
        dtmfBtn = (Button) findViewById(R.id.dtmfBtn);

        Button logout;
        logout = (Button) findViewById(R.id.logoutBtn);

        phoneNumberText = (EditText) findViewById(R.id.editText1);
        callBtn = (Button) findViewById(R.id.callBtn);


       /*
        * Setup the broadcast receiver to be notified of FCM Token updates
        * or incoming call invite in this Activity.
        */

        voiceBroadcastReceiver = new VoiceBroadcastReceiver();
        registerReceiver();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent service = new Intent(getApplicationContext(), KeepAliveService.class);
        getApplicationContext().startService(service);

    }


    private void registerReceiver() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_INCOMING_CALL);
        LocalBroadcastManager.getInstance(this).registerReceiver(voiceBroadcastReceiver, intentFilter);

    }

    public void makeCall(View view) {

        if (phoneNumberText.getText().toString().equals("")) {

            Toast.makeText(this, "Please enter valid number", Toast.LENGTH_SHORT).show();


        } else {

            if (callBtn.getText().toString().equals("Make a call")) {

                callBtn.setText("Hangup");

                outgoing = Phone.getInstance(this).createOutgoingCall();

                outgoing.call(phoneNumberText.getText().toString());

            } else {

                Log.d("Outgoing", "Hangup");

                outgoing.hangup();

                VoiceActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        callBtn.setText("Make a call");

                    }
                });
            }

        }
    }

    public void answerIncomingCall(View view) {

        incoming.answer();

        VoiceActivity.this.runOnUiThread(new Runnable()
        {
            public void run()
            {
                declineBtn.setText("Hangup");

            }
        });

    }

    public void declineIncomingCall(View view) {

        if (callBtn.getText().toString().equals("Decline")) {

            incoming.reject();

        }else
        {
            incoming.hangup();

        }

        VoiceActivity.this.runOnUiThread(new Runnable()
        {
            public void run()
            {
                declineBtn.setText("Decline");

            }
        });

    }

    public void muteIncomingCall(View view) {

        if (muteBtn.getText().toString().equals("Mute")) {

            if (incoming != null) {
                incoming.mute();
            } else {
                outgoing.mute();
            }

            VoiceActivity.this.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    muteBtn.setText("Unmute");
                    Log.d("Set Text","Unmute");


                }
            });

        }else {

            VoiceActivity.this.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    muteBtn.setText("Mute");
                    Log.d("Set Text","Mute");

                }
            });

            if (incoming != null) {
                incoming.unmute();
            } else {
                outgoing.unmute();
            }

        }

    }

    public void sendDtmf(View view) {

        if (dtmfText.getText().toString().equals("")) {

            Toast.makeText(this, "InValid DTMF", Toast.LENGTH_SHORT).show();

        }else{

            if (incoming != null) {

                incoming.sendDigits(dtmfText.getText().toString());
            } else {
                outgoing.sendDigits(dtmfText.getText().toString());

            }

            dtmfText.setText("");

        }
    }

    public void logout(View view) {
        endpoint.logout();
    }

    private class VoiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_INCOMING_CALL)) {
               /*
                * Handle the incoming call invite
                */
                handleIncomingCallIntent(intent);
            }
        }
    }

    private void handleIncomingCallIntent(Intent intent) {

        incoming = Phone.getInstance(this).incoming;

        alertDialog = createIncomingCallDialog(VoiceActivity.this,
                incoming,
                answerCallClickListener(),
                cancelCallClickListener());
        alertDialog.show();

    }

    public static AlertDialog createIncomingCallDialog(
            Context context,
            Incoming incoming,
            DialogInterface.OnClickListener answerCallClickListener,
            DialogInterface.OnClickListener cancelClickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Incoming Call");
        alertDialogBuilder.setPositiveButton("Accept", answerCallClickListener);
        alertDialogBuilder.setNegativeButton("Reject", cancelClickListener);
        alertDialogBuilder.setMessage(incoming.getFromContact() + " is calling.");
        return alertDialogBuilder.create();
    }

    private DialogInterface.OnClickListener answerCallClickListener() {
        return new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                incoming.answer();

                VoiceActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        declineBtn.setText("Hangup");
                        alertDialog.dismiss();


                    }
                });
            }
        };
    }

    private DialogInterface.OnClickListener cancelCallClickListener() {
        return new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                incoming.reject();

                VoiceActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        declineBtn.setText("Decline");
                        alertDialog.dismiss();


                    }
                });
            }
        };
    }

    public void onLogin() {

        Log.d("PlivoInbound", "Logging in");

    }

    public void onLogout() {

        Log.d("PlivoInbound", "Logged out");
        finish();

    }

    public void onLoginFailed() {

        Log.d("PlivoInbound", "Login failed");

    }

    public void onIncomingCall(Incoming incoming) {

        Log.d("PlivoInbound", "Incoming call received");

        this.incoming = incoming;

        this.incoming.answer();

        VoiceActivity.this.runOnUiThread(new Runnable()
        {
            public void run()
            {
                declineBtn.setText("Decline");

            }
        });
    }

    public void onIncomingCallHangup(Incoming incoming) {

        VoiceActivity.this.runOnUiThread(new Runnable()
        {
            public void run()
            {

                declineBtn.setText("Decline");

            }
        });
    }

    public void onIncomingCallRejected(Incoming incoming) {


        VoiceActivity.this.runOnUiThread(new Runnable()
        {
            public void run()
            {
                declineBtn.setText("Decline");

            }
        });
    }

    public void onOutgoingCall(Outgoing outgoing) {

    }

    public void onOutgoingCallAnswered(Outgoing outgoing) {

    }

    public void onOutgoingCallRejected(Outgoing outgoing) {

        VoiceActivity.this.runOnUiThread(new Runnable()
        {
            public void run()
            {
                callBtn.setText("Make a call");

            }
        });
    }

    public void onOutgoingCallHangup(Outgoing outgoing) {

        VoiceActivity.this.runOnUiThread(new Runnable()
        {
            public void run()
            {
                callBtn.setText("Make a call");

            }
        });
    }

    public void onOutgoingCallInvalid(Outgoing outgoing) {

        VoiceActivity.this.runOnUiThread(new Runnable()
        {
            public void run()
            {
                callBtn.setText("Make a call");

            }
        });
    }

    public void onIncomingDigitNotification(String digits) {



        VoiceActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                {
                    Log.d("DTMF value is: ",digits);
                    switch (digits) {
                        case "-6":
                            Toast.makeText(VoiceActivity.this, "Incoming DTMF: " + "*", Toast.LENGTH_SHORT).show();
                            break;
                        case "-13":
                            Toast.makeText(VoiceActivity.this, "Incoming DTMF: " + "#", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(VoiceActivity.this, "Incoming DTMF: " + digits, Toast.LENGTH_SHORT).show();
                            break;
                    }

                }

            }
        });

    }


    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            //super.onBackPressed();
            System.exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(mRunnable, 2000);
    }

}

