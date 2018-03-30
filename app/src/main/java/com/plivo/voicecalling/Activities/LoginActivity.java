package com.plivo.voicecalling.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;
import com.plivo.voicecalling.Helpers.EndPointListner;
import com.plivo.voicecalling.Helpers.Phone;
import com.plivo.voicecalling.R;

public class LoginActivity extends AppCompatActivity implements EndPointListner{

    public Endpoint endpoint;

    SharedPreferences sharedPreferences;

    EditText username,password;
    Button loginBtn;
    TextView terminalLogTextView;

    String usernameStr, passwordStr;

    ProgressDialog progress;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestAppPermissions();

        setContentView(R.layout.activity_login);

        progress = new ProgressDialog(LoginActivity.this);

        username = (EditText) findViewById(R.id.editText1);
        password = (EditText) findViewById(R.id.editText2);
        loginBtn = (Button) findViewById(R.id.loginBtn);

        terminalLogTextView = (TextView)findViewById(R.id.terminalOutput);
        terminalLogTextView.setMovementMethod(new ScrollingMovementMethod());
        terminalLogTextView.setBackgroundColor(1);
        terminalLogTextView.append("\n Init");


        endpoint = Phone.getInstance(this).endpoint;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        usernameStr = sharedPreferences.getString("username","");

        if (usernameStr.length() > 0) {

            passwordStr = sharedPreferences.getString("password","");

            this.showProgressView();

            Phone.getInstance(this).login(usernameStr, passwordStr);

        }

    }

    public void showProgressView()
    {
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }


    public void login(View view) {

        if (username.getText().toString().equals("") || password.getText().toString().equals("")) {

            Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();

        } else {

            Log.d("PlivoInbound", "Trying to log in");

            this.showProgressView();
            usernameStr = username.getText().toString();
            passwordStr = password.getText().toString();
            Phone.getInstance(this).login(usernameStr, passwordStr);
        }
    }

    //Endpoint Listeners

    public void onLogin() {

        Log.d("PlivoInbound", "Logging in");

        LoginActivity.this.runOnUiThread(new Runnable()
        {
            public void run()
            {
                terminalLogTextView.append("\n Login Success");

                // To dismiss the dialog
                progress.dismiss();

                sharedPreferences.edit().putString("username", usernameStr).apply();
                sharedPreferences.edit().putString("password", passwordStr).apply();

            }
        });

        Log.d("Listner","Phone");

        Intent intent = new Intent(this, VoiceActivity.class);
        startActivity(intent);

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

    public  void  requestAppPermissions()
    {
        if (Build.VERSION.SDK_INT < 23) {
            // your code

        } else {

            requestPermissions(new String[]{
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.RECORD_AUDIO,Manifest.permission.MODIFY_AUDIO_SETTINGS,
                            Manifest.permission.PROCESS_OUTGOING_CALLS,Manifest.permission.WRITE_SETTINGS,
                            Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.WAKE_LOCK,
                            Manifest.permission.VIBRATE,Manifest.permission.READ_LOGS,Manifest.permission.USE_SIP,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }

    }
}
