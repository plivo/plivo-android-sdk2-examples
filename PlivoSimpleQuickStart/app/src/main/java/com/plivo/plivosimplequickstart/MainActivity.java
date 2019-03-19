package com.plivo.plivosimplequickstart;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.EventListener;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.PermissionChecker;

import static com.plivo.plivosimplequickstart.Utils.HH_MM_SS;
import static com.plivo.plivosimplequickstart.Utils.MM_SS;

public class MainActivity extends AppCompatActivity implements EventListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_CODE = 21;

    private enum STATE { IDLE, RINGING, ANSWERED, HANGUP, REJECTED, INVALID }

    private Endpoint plivoEndpoint;

    private AlertDialog alertDialog;

    private Timer callTimer;

    private int tick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                requestPermissions(new String[] { Manifest.permission.RECORD_AUDIO }, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults != null && grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init() {
        if (!endpoint().getRegistered()) {
            loginWithToken();
        }
    }

    private void loginWithToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            Log.d(TAG, "fcm token " + newToken);
            if (endpoint().login(Utils.USERNAME, Utils.PASSWORD, newToken)) {
                // if already logged in
//                updateUI(STATE.IDLE, null);
            } else {
                // if not logged in yet, handle at onLogin()
            }
        });
    }

    private void logout() {
        endpoint().logout();
    }

    private void showOutCallUI(STATE state, Outgoing outgoing) {
        runOnUiThread(() -> {
            if (alertDialog != null) alertDialog.dismiss();

            String title = state.name() + " " + (outgoing != null ? Utils.to(outgoing.getToContact()) : "");
            CharSequence btnText = getString(R.string.call);
            boolean cancelable = true;
            boolean showAlert = false;
            switch (state) {
                case IDLE:
                    title = getString(R.string.enter_outgoing);
                    showAlert = true;
                    break;

                case ANSWERED:
                case RINGING:
                    cancelable = false;
                    showAlert = true;
                    btnText = getString(R.string.end_call);

                    if (state == STATE.ANSWERED) startTimer();
                    break;
            }

            if (showAlert) {
                alertDialog = new AlertDialog.Builder(this)
                        .setTitle(title)
                        .setView(R.layout.dialog_outgoing_content_view)
                        .setCancelable(cancelable)
                        .setNeutralButton(btnText, (dialog, which) -> {
                            if (state == STATE.IDLE) {
                                makeCall();
                            } else {
                                outgoing.hangup();
                            }
                        })
                        .show();
            }
        });
    }

    private void startTimer() {
        cancelTimer();

        callTimer = new Timer(false);
        callTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (alertDialog != null) {
                        int hours = (int) TimeUnit.SECONDS.toHours(tick);
                        int minutes = (int) TimeUnit.SECONDS.toMinutes(tick-=TimeUnit.HOURS.toSeconds(hours));
                        int seconds = (int) (tick-TimeUnit.MINUTES.toSeconds(minutes));
                        alertDialog.setMessage(hours > 0 ? String.format(HH_MM_SS, hours, minutes, seconds) : String.format(MM_SS, minutes, seconds));
                    }
                });
            }
        }, TimeUnit.SECONDS.toMillis(1));
    }

    private void cancelTimer() {
        if (callTimer != null) callTimer.cancel();
    }

    private void makeCall() {
        Outgoing outgoing = endpoint().createOutgoingCall();
        if (outgoing != null) {
            outgoing.call(((AppCompatEditText) alertDialog.findViewById(R.id.edit_number)).getText().toString());
        }
    }

    public void onClickBtnMakeCall(View view) {
        updateUI(STATE.IDLE, null);
    }

    private void updateUI(STATE state, Object data) {
        runOnUiThread(() -> {

            switch (state) {
                case IDLE:
                    ((AppCompatTextView) findViewById(R.id.logged_in_as)).setText(Utils.USERNAME);
                    findViewById(R.id.call_btn).setEnabled(true);
                    break;
            }

            ((AppCompatTextView) findViewById(R.id.status)).setText(state.name());

            if (data != null) {
                if (data instanceof Outgoing) {
                    // handle outgoing
                    showOutCallUI(state, (Outgoing) data);
                } else {
                    // handle incoming

                }
            } else {
                showOutCallUI(state, null);
            }
        });
    }

    private Endpoint endpoint() {
        return plivoEndpoint != null? plivoEndpoint: (plivoEndpoint = Endpoint.newInstance(BuildConfig.DEBUG, this));
    }

    @Override
    public void onLogin() {
        Log.d(TAG, "onLogin success");
        updateUI(STATE.IDLE, null);
    }

    @Override
    public void onLogout() {
        Log.d(TAG, "onLogout success");
        plivoEndpoint.resetEndpoint();
        plivoEndpoint = null;
        runOnUiThread(() -> {
            Toast.makeText(this, R.string.logout_success, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void onLoginFailed() {
        Log.e(TAG, "onLoginFailed");
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
        Log.d(TAG, "onOutgoingCall Ringing");
        updateUI(STATE.RINGING, outgoing);
    }

    @Override
    public void onOutgoingCallAnswered(Outgoing outgoing) {
        Log.d(TAG, "onOutgoingCall Answered");
        updateUI(STATE.ANSWERED, outgoing);
    }

    @Override
    public void onOutgoingCallRejected(Outgoing outgoing) {
        Log.d(TAG, "onOutgoingCall Rejected");
        updateUI(STATE.REJECTED, outgoing);
    }

    @Override
    public void onOutgoingCallHangup(Outgoing outgoing) {
        Log.d(TAG, "onOutgoingCall Hangup");
        updateUI(STATE.HANGUP, outgoing);
    }

    @Override
    public void onOutgoingCallInvalid(Outgoing outgoing) {
        Log.d(TAG, "onOutgoingCall Invalid");
        updateUI(STATE.INVALID, outgoing);
    }

}
