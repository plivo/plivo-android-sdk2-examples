package com.plivo.plivoincomingcall.screens.dial;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.plivo.plivoincomingcall.App;
import com.plivo.plivoincomingcall.BaseActivity;
import com.plivo.plivoincomingcall.R;
import com.plivo.plivoincomingcall.dagger2.DaggerViewComponent;
import com.plivo.plivoincomingcall.dagger2.ViewContext;
import com.plivo.plivoincomingcall.model.Call;
import com.plivo.plivoincomingcall.screens.login.LoginActivity;
import com.plivo.plivoincomingcall.utils.AlarmUtils;
import com.plivo.plivoincomingcall.utils.AlertUtils;
import com.plivo.plivoincomingcall.utils.Constants;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import butterknife.ButterKnife;

public class DialActivity extends BaseActivity {
    private static final int REQUEST_CODE_DIAL_PERMISSIONS = 24;
    private static final String TAG = DialActivity.class.getSimpleName();

    @Inject
    AlertUtils alertUtils;

    @Inject
    AlarmUtils alarmUtils;

    DialViewModel viewModel;

    @Inject
    OngoingCallFragment ongoingCallFragment;

    @Inject
    IncomingCallFragment incomingCallFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial);
        ButterKnife.bind(this);
        viewModel = ViewModelProviders.of(this).get(DialViewModel.class);
        DaggerViewComponent.builder().viewContext(new ViewContext(this)).build().inject(this);

        showOngoing();
    }



    @Override
    protected void onStart() {
        super.onStart();
        alertUtils.requestDialPermissionsRequired(REQUEST_CODE_DIAL_PERMISSIONS);

        viewModel.incomingCallObserver().observe(this, call -> {
            updateUi(call);
        });

        if (getIntent().getBooleanExtra(Constants.EXTRA_INCOMING_CALL, false)) {
            viewModel.triggerIncomingCall();
        }
    }

    private void showIncoming() {
        showFragment(incomingCallFragment);
    }

    private void showOngoing() {
        showFragment(ongoingCallFragment);
    }

    @Override
    protected void onDestroy() {
        if (viewModel.isLoggedIn()) {
            ((App) getApplication()).startBakgroundService();
        }
        super.onDestroy();
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

    private void logout() {
        viewModel.logoutObserver().observe(this, object -> {
            alarmUtils.cancelRepeatingAlarm();
            ((App) getApplication()).stopBakgroundService();
            startActivity(new Intent(this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        });
        viewModel.logout();
    }

    private void updateUi(Call call) {
        if (call == null) return;

        if (call.isRinging()) {
            showIncoming();
        } else {
            showOngoing();
        }
    }

}
