package com.plivo.plivooutgoingcall.screens.dial;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.plivo.plivooutgoingcall.R;
import com.plivo.plivooutgoingcall.dagger2.DaggerViewComponent;
import com.plivo.plivooutgoingcall.dagger2.ViewContext;
import com.plivo.plivooutgoingcall.plivo.layer.PlivoCallState;
import com.plivo.plivooutgoingcall.utils.AlertUtils;
import com.plivo.plivooutgoingcall.widgets.CallButton;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class DialActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_DIAL_PERMISSIONS = 24;

    @BindView(R.id.number_editText)
    AppCompatEditText numberEditText;

    @BindView(R.id.call_btn)
    CallButton call_btn;

    @Inject
    AlertUtils alertUtils;

    DialViewModel dialViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial);
        ButterKnife.bind(this);

        dialViewModel = ViewModelProviders.of(this).get(DialViewModel.class);
        DaggerViewComponent.builder().viewContext(new ViewContext(this)).build().inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        alertUtils.requestDialPermissionsRequired(REQUEST_CODE_DIAL_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_DIAL_PERMISSIONS:
                if (grantResults != null && grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do init
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.dial_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            default:
                return true;
        }
    }

    @OnCheckedChanged(R.id.mute_btn)
    public void onClickMuteBtn(CompoundButton btn, boolean isChecked) {
        if (isChecked) {
            dialViewModel.mute();
        } else {
            dialViewModel.unmute();
        }
    }

    @OnTextChanged(R.id.number_editText)
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        call_btn.setEnabled(!TextUtils.isEmpty(s));
        if (!TextUtils.isEmpty(s)) call_btn.setState(PlivoCallState.OUT_CALL_STATE.IDLE);
    }

    @OnClick(R.id.call_btn)
    public void onClickCallBtn() {
        switch (call_btn.getState()) {
            case ANSWERED:
            case RINGING:
                endCall();
                break;
            default:
                String phone_num = numberEditText.getText().toString();
//                phone_num = "919740253357";
                call(phone_num);
                break;
        }

    }

    private void call(String phone_num) {
        dialViewModel.call(phone_num).observe(this, state -> {
            call_btn.setState(state);
            runOnUiThread(() -> updateUi());
        });
    }

    private void endCall() {
        dialViewModel.endCall().observe(this, state -> {
            call_btn.setState(state);
            runOnUiThread(() -> updateUi());
        });
    }

    private void updateUi() {
        switch (call_btn.getState()) {
            case INVALID:
                alertUtils.showToast("Invalid number");
                call_btn.setText("Call");
                call_btn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
                break;
            case REJECTED:
                alertUtils.showToast("Call Rejected");
                call_btn.setText("Call");
                call_btn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
                break;
            case ANSWERED:
                alertUtils.showToast("Call Answered");
                call_btn.setText("End Call");
                call_btn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
                break;
            case HANGUP:
                alertUtils.showToast("Call Hangup");
                call_btn.setText("Call");
                call_btn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray));
                break;
            case RINGING:
                alertUtils.showToast("Call Ringing...");
                call_btn.setText("End Call");
                call_btn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
                break;
            case IDLE:
                call_btn.setText("Call");
                call_btn.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
                break;

            default: alertUtils.showToast("Unknown Error"); break;
        }
    }

    private void logout() {
        dialViewModel.logout().observe(this, object -> {
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        alertUtils.showAlertTwoButton("Logout",
                "Do you want to logout?",
                getString(android.R.string.yes),
                (dialog, which) -> {
                    // positive button click
                    logout();
                },
                getString(android.R.string.cancel),
                null
        );
    }
}
