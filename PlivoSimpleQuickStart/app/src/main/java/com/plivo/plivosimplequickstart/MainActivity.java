package com.plivo.plivosimplequickstart;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.PermissionChecker;

import com.google.firebase.iid.FirebaseInstanceId;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;
import com.plivo.plivosimplequickstart.PlivoBackEnd.STATE;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.plivo.plivosimplequickstart.Utils.HH_MM_SS;
import static com.plivo.plivosimplequickstart.Utils.MM_SS;

public class MainActivity extends AppCompatActivity implements PlivoBackEnd.BackendListener {
    private static final int PERMISSIONS_REQUEST_CODE = 21;

    private AlertDialog alertDialog;

    private Timer callTimer;

    private int tick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                requestPermissions(new String[] { Manifest.permission.RECORD_AUDIO }, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            init();
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
        registerBackendListener();
        loginWithToken();
    }

    private void registerBackendListener() {
        ((App) getApplication()).backend().setListener(this);
    }

    private void loginWithToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult ->
                ((App) getApplication()).backend().login(instanceIdResult.getToken()));
    }

    private void logout() {
        ((App) getApplication()).backend().logout();
    }

    /**
     * Display & Handle Outgoing Calls
     * @param state
     * @param outgoing
     */
    private void showOutCallUI(STATE state, Outgoing outgoing) {
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
                            cancelTimer();
                            outgoing.hangup();
                        }
                    })
                    .show();

            if (state == STATE.ANSWERED) startTimer();
        }

        if (alertDialog != null) {
            // DTMF handle
            AppCompatEditText editBox = alertDialog.findViewById(R.id.edit_number);
            if (editBox != null) {
                editBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void afterTextChanged(Editable s) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (state != STATE.ANSWERED || outgoing == null) return;

                        if (!TextUtils.isEmpty(s) && before < count) {
                            outgoing.sendDigits(Character.toString(s.charAt(s.length() - 1)));
                        }
                    }
                });
            }

            // stop timer
            alertDialog.setOnDismissListener(dialog -> {
                if (state == STATE.ANSWERED) cancelTimer();
            });
        }

    }

    /**
     * Display & Handle Incoming Calls
     * @param state
     * @param incoming
     */
    private void showInCallUI(STATE state, Incoming incoming) {
        if (alertDialog != null) alertDialog.dismiss();

        String title = state.name() + " " + (incoming != null ? Utils.from(incoming.getFromContact(), incoming.getFromSip()) : "");

        switch (state) {
            case ANSWERED:
                alertDialog = new AlertDialog.Builder(this)
                        .setTitle(title)
                        .setView(R.layout.dialog_outgoing_content_view)
                        .setCancelable(false)
                        .setNeutralButton(R.string.end_call, (dialog, which) -> {
                            cancelTimer();
                            incoming.hangup();
                        })
                        .show();
                startTimer();
                break;

            case RINGING:
                alertDialog = new AlertDialog.Builder(this)
                        .setTitle(title)
                        .setView(R.layout.dialog_outgoing_content_view)
                        .setCancelable(false)
                        .setNegativeButton(R.string.reject, (dialog, which) -> incoming.reject())
                        .setPositiveButton(R.string.answer, (dialog, which) -> {
                            incoming.answer();
                            updateUI(STATE.ANSWERED, incoming);
                        })
                        .show();
                break;
        }

        if (alertDialog != null) {
            // DTMF handle
            AppCompatEditText editBox = alertDialog.findViewById(R.id.edit_number);
            if (editBox != null) {
                editBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void afterTextChanged(Editable s) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (state != STATE.ANSWERED || incoming == null) return;

                        if (!TextUtils.isEmpty(s) && before < count) {
                            incoming.sendDigits(Character.toString(s.charAt(s.length() - 1)));
                        }
                    }
                });
            }

            // stop timer
            alertDialog.setOnDismissListener(dialog -> {
                if (state == STATE.ANSWERED) cancelTimer();
            });
        }
    }

    private void startTimer() {
        cancelTimer();

        callTimer = new Timer(false);
        callTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (alertDialog != null) {
                        int hours = (int) TimeUnit.SECONDS.toHours(tick);
                        int minutes = (int) TimeUnit.SECONDS.toMinutes(tick-=TimeUnit.HOURS.toSeconds(hours));
                        int seconds = (int) (tick-TimeUnit.MINUTES.toSeconds(minutes));
                        String text = hours > 0 ? String.format(HH_MM_SS, hours, minutes, seconds) : String.format(MM_SS, minutes, seconds);
                        AppCompatTextView timerTextView = alertDialog.findViewById(R.id.timer_text);
                        if (timerTextView != null) {
                            timerTextView.setVisibility(View.VISIBLE);
                            timerTextView.setText(text);
                            tick++;
                        }
                    }
                });
            }
        }, 100, TimeUnit.SECONDS.toMillis(1));
    }

    private void cancelTimer() {
        if (callTimer != null) callTimer.cancel();
        tick = 0;
    }

    private void makeCall() {
        Outgoing outgoing = ((App) getApplication()).backend().getOutgoing();
        if (outgoing != null) {
            HashMap<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("X-PH-Header1", "12345");
            extraHeaders.put("X-ph-Header2", "34567");
            extraHeaders.put("x-ph-Header3", "57");
            extraHeaders.put("x-p-Header3", "567");


            outgoing.callH(((AppCompatEditText) alertDialog.findViewById(R.id.edit_number)).getText().toString(), extraHeaders);

        }
    }
    public void showRatingWindow(){
        RatingBar ratingBar;
        TextView star;
        setContentView(R.layout.rating);
        ratingBar = (RatingBar) findViewById(R.id.star);
        star = (TextView) findViewById(R.id.star_count);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                float ratedValue;
                ratedValue = ratingBar.getRating();
                star.setText("Your Rating : " + (int)ratedValue + "/5");
                LinearLayout one = (LinearLayout) findViewById(R.id.LinearLayout);

                if (ratedValue==5) {
                    EditText comments = (EditText) findViewById(R.id.comments);
                    comments.getText().clear();
                    one.setVisibility(View.GONE);
                }
                else{
                    one.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    public void submitCallQualityFeedback(){
        Boolean addLog= ((CheckBox) findViewById(R.id.add_log)).isChecked();
        ArrayList <String> issueList = new ArrayList<String>();
        if (((CheckBox) findViewById(R.id.audio_lag)).isChecked()){
            issueList.add( (((CheckBox) findViewById(R.id.audio_lag)).getText()).toString());
        }
        if (((CheckBox) findViewById(R.id.broken_audio)).isChecked()){
            issueList.add( (((CheckBox) findViewById(R.id.broken_audio)).getText()).toString());
        }
        if (((CheckBox) findViewById(R.id.call_dropped)).isChecked()){
            issueList.add( (((CheckBox) findViewById(R.id.call_dropped)).getText()).toString());
        }
        if (((CheckBox) findViewById(R.id.high_connect_time)).isChecked()){
            issueList.add( (((CheckBox) findViewById(R.id.high_connect_time)).getText()).toString());
        }
        if (((CheckBox) findViewById(R.id.low_audio_level)).isChecked()){
            issueList.add( (((CheckBox) findViewById(R.id.low_audio_level)).getText()).toString());
        }
        if (((CheckBox) findViewById(R.id.callerid_issues)).isChecked()){
            issueList.add( (((CheckBox) findViewById(R.id.callerid_issues)).getText()).toString());
        }
        if (((CheckBox) findViewById(R.id.echo)).isChecked()){
            issueList.add( (((CheckBox) findViewById(R.id.echo)).getText()).toString());
        }
        RatingBar ratingBar = (RatingBar) findViewById(R.id.star);
        Integer ratedValue = (int) (ratingBar.getRating());
        String comments = ((EditText) findViewById(R.id.comments)).getText().toString();
        if(ratedValue==5) {
            issueList.clear();
            addLog=false;
        }
        if (ratedValue==0){
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Star rating can't be empty")
                    .setCancelable(true)
                    .setNeutralButton("Ok", (dialog, which) -> {
                        showRatingWindow();
                    })
                    .show();
        }
        else if (ratedValue<5 && issueList.size()==0){
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Atleast one issue is mandatory for feedback")
                    .setCancelable(true)
                    .setNeutralButton("Ok", (dialog, which) -> {
                        showRatingWindow();
                    })
                    .show();

        }
        else {
            ((App) getApplication()).backend().submitCallQualityFeedback(ratedValue, addLog, comments, issueList);
            setContentView(R.layout.activity_main);
            updateUI(STATE.IDLE, null);
        }

    }

    public void onClickBtnMakeCall(View view) {
        showOutCallUI(STATE.IDLE, null);
    }

    public void onClickBtnFeedback(View view) {
        showRatingWindow();
    }

    public void onClickSubmitFeedback(View view) {
        submitCallQualityFeedback();
    }

    public void onClickSkip(View view){
        setContentView(R.layout.activity_main);
        updateUI(STATE.IDLE, null);
    }

    private void updateUI(PlivoBackEnd.STATE state, Object data) {
        if(state.equals(STATE.REJECTED) || state.equals(STATE.HANGUP) || state.equals(STATE.INVALID)){
            if (data != null) {
                if (data instanceof Outgoing) {
                    // handle outgoing
                    showOutCallUI(state, (Outgoing) data);
                } else {
                    // handle incoming
                    showInCallUI(state, (Incoming) data);
                }
            }
            showRatingWindow();
        }
        else {

            if(findViewById(R.id.call_btn) ==null || findViewById(R.id.feedback) == null ||  findViewById(R.id.logged_in_as) == null || findViewById(R.id.status)==null){
                if (data != null) {
                    if (data instanceof Outgoing) {
                        // handle outgoing
                        showOutCallUI(state, (Outgoing) data);
                    } else {
                        // handle incoming
                        showInCallUI(state, (Incoming) data);
                    }
                }
            }else {
                findViewById(R.id.call_btn).setEnabled(true);
                findViewById(R.id.feedback).setEnabled(true);
                ((AppCompatTextView) findViewById(R.id.logged_in_as)).setText(Utils.USERNAME);
                ((AppCompatTextView) findViewById(R.id.status)).setText(state.name());

                if (data != null) {
                    if (data instanceof Outgoing) {
                        // handle outgoing
                        showOutCallUI(state, (Outgoing) data);
                    } else {
                        // handle incoming
                        showInCallUI(state, (Incoming) data);
                    }
                }
            }
        }
    }

    @Override
    public void onLogin(boolean success) {
        runOnUiThread(() -> {
            if (success) {
                updateUI(STATE.IDLE, null);
            } else {
                Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLogout() {
        runOnUiThread(() -> {
            Toast.makeText(this, R.string.logout_success, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void onIncomingCall(Incoming data, PlivoBackEnd.STATE callState) {
        runOnUiThread(() -> updateUI(callState, data));
    }

    @Override
    public void onOutgoingCall(Outgoing data, PlivoBackEnd.STATE callState) {
        runOnUiThread(() -> updateUI(callState, data));
    }

    @Override
    public void onIncomingDigit(String digit) {
        runOnUiThread(() -> Toast.makeText(this, String.format(getString(R.string.dtmf_received), digit), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void mediaMetrics(HashMap messageTemplate){
        if(messageTemplate!=null && messageTemplate.containsKey("level") && messageTemplate.containsKey("type") && messageTemplate.containsKey("active")) {
            if((Boolean)messageTemplate.get("active")) {
                runOnUiThread(() -> Toast.makeText(this, String.format(messageTemplate.get("level").toString() + " | " + messageTemplate.get("type").toString()), Toast.LENGTH_LONG).show());
            }
        }
    }
}
