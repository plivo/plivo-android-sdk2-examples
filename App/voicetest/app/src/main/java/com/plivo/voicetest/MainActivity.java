package com.plivo.voicetest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.EventListener;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;

public class MainActivity extends AppCompatActivity implements EventListener {

    public Endpoint endpoint = new Endpoint(true, this);
    public Outgoing outgoing = new Outgoing(endpoint);

    public Incoming incomingCall;

    SharedPreferences sharedPreferences;

    EditText username,password;
    Button loginBtn;

    EditText phoneNumberText;
    Button callBtn, acceptBtn;

    TextView terminalLogTextView;

    Boolean isItOutgoingCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.requestPermissions();

        username = (EditText) findViewById(R.id.editText1);
        password = (EditText) findViewById(R.id.editText2);
        loginBtn = (Button) findViewById(R.id.loginBtn);

        phoneNumberText = (EditText)findViewById(R.id.editText3);
        phoneNumberText.setVisibility(View.GONE);
        callBtn = (Button) findViewById(R.id.callBtn);
        callBtn.setVisibility(View.GONE);

        acceptBtn = (Button) findViewById(R.id.acceptBtn);
        acceptBtn.setVisibility(View.GONE);

        terminalLogTextView = (TextView)findViewById(R.id.terminalOutput);
        terminalLogTextView.setMovementMethod(new ScrollingMovementMethod());
        terminalLogTextView.setBackgroundColor(1);

        terminalLogTextView.append("\n Init");

        isItOutgoingCall = false;

    }


    public void login(View view) {

        if (username.getText().toString().equals("") || password.getText().toString().equals("")) {

            Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();

        } else {

            terminalLogTextView.append("\n Trying to Log In");

            Log.d("PlivoInbound", "Trying to log in");
            endpoint.login(username.getText().toString(), password.getText().toString());
        }
    }


    public void onLogin() {

        Log.d("PlivoInbound", "Logging in");

        MainActivity.this.runOnUiThread(new Runnable() {
            public void run()
            {
                terminalLogTextView.append("\n Log in - Success");

                username.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                loginBtn.setVisibility(View.GONE);

                phoneNumberText.setVisibility(View.VISIBLE);
                callBtn.setVisibility(View.VISIBLE);

                //Toast.makeText(this, "SIP registration done successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void makeCall(View view) {

        if(phoneNumberText.getText().toString().equals("")){

            Toast.makeText(this, "Please enter valid number", Toast.LENGTH_SHORT).show();


        }else {

            if (callBtn.getText().toString().equals("Make a call")) {

                callBtn.setText("Hangup");

                isItOutgoingCall = true;

                terminalLogTextView.append("\n Outbound: " +phoneNumberText.getText().toString()+"Calling...");

                outgoing = endpoint.createOutgoingCall();
                Log.d("PlivoOutbound", "Create outbound call object");

                outgoing.call(phoneNumberText.getText().toString());

            } else {

                this.endCall();
            }


        }
    }

    public void endCall()
    {
        if (isItOutgoingCall)
        {
            isItOutgoingCall = false;

            Log.d("Outgoing", "Hangup");
            outgoing.hangup();

            MainActivity.this.runOnUiThread(new Runnable() {
                public void run()
                {
                    acceptBtn.setVisibility(View.GONE);
                    phoneNumberText.setHint("SIP Username or Phone");
                    callBtn.setText("Make a call");
                    terminalLogTextView.append("\n Outgoing Call - Hangup");

                }
            });
        }
        else {

            Log.d("Incoming", "Hangup");
            incomingCall.hangup();

            MainActivity.this.runOnUiThread(new Runnable() {
                public void run()
                {
                    acceptBtn.setVisibility(View.GONE);
                    phoneNumberText.setHint("SIP Username or Phone");
                    callBtn.setText("Make a call");
                    terminalLogTextView.append("\n Incoming Call - Hangup");
                }
            });
        }
    }

    public void answerIncomingCall(View view) {

        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                incomingCall.answer();
                acceptBtn.setVisibility(View.GONE);
                callBtn.setText("Hangup");
            }
        });

    }

    public void onLogout() {

        Log.d("PlivoInbound", "Logged out");

        MainActivity.this.runOnUiThread(new Runnable() {
            public void run()
            {
                terminalLogTextView.append("\n Logged out");
            }
        });

    }

    public void onLoginFailed() {

        Log.d("PlivoInbound", "Login failed");

        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                terminalLogTextView.append("\n Log in - Failed");
            }
        });

    }

    public void onIncomingCall(Incoming incoming) {

        Log.v("PlivoInbound", "Inbound Call...");

        incomingCall = incoming;

        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {

                terminalLogTextView.append("\n Inbound Call...");
                acceptBtn.setVisibility(View.VISIBLE);
                callBtn.setText("Hangup");

            }
        });

    }

    public void onIncomingCallHangup(Incoming incoming) {
        Log.d("PlivoInbound", "Call hanging up");

        this.endCall();

        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                terminalLogTextView.append("\n PlivoInbound - Call hanging up");
            }
        });

    }

    public void onIncomingCallRejected(Incoming incoming) {

        this.endCall();

        Log.d("onIncomingCall", "Rejected");

        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                terminalLogTextView.append("\n PlivoInbound - Rejected");
            }
        });

    }

    public void onOutgoingCall(Outgoing outgoing) {

        Log.d("onOutgoingCall", "onOutgoingCall");

        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                terminalLogTextView.append("\n onOutgoingCall");

            }
        });
    }

    public void onOutgoingCallAnswered(Outgoing outgoing) {
        Log.d("onOutgoingCall", "Answered");
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                terminalLogTextView.append("\n onOutgoingCall - Answered");
            }
            });
    }

    public void onOutgoingCallRejected(Outgoing outgoing) {
        Log.d("onOutgoingCall", "Rejected");

        this.endCall();

        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                terminalLogTextView.append("\n onOutgoingCall - Rejected");
            }
        });
    }

    public void onOutgoingCallHangup(Outgoing outgoing) {
        Log.d("onOutgoingCall", "Hangup");

        this.endCall();

        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                terminalLogTextView.append("\n onOutgoingCall - Hangup");
            }
        });

    }

    public void onOutgoingCallInvalid(Outgoing outgoing){
        Log.d("onOutgoingCall", "Invalid");

        this.endCall();

        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                terminalLogTextView.append("\n onOutgoingCall - Invalid");
            }
            });
    }

    public void onIncomingDigitNotification(String digits){
        Log.d("onIncoming", "DigitNotification");

        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                terminalLogTextView.append("\n PlivoInbound - DigitNotification");
            }
        });
    }

    public  void  requestPermissions()
    {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.MODIFY_AUDIO_SETTINGS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.PROCESS_OUTGOING_CALLS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_SETTINGS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_SETTINGS},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_WIFI_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_WIFI_STATE},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WAKE_LOCK)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WAKE_LOCK},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.VIBRATE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.VIBRATE},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_LOGS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_LOGS},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.USE_SIP)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.USE_SIP},1);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

    }
}
