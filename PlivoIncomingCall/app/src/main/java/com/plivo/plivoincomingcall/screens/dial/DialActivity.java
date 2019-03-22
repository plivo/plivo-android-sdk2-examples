package com.plivo.plivoincomingcall.screens.dial;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.plivo.plivoincomingcall.BaseActivity;
import com.plivo.plivoincomingcall.BaseFragment;
import com.plivo.plivoincomingcall.R;
import com.plivo.plivoincomingcall.dagger2.DaggerViewComponent;
import com.plivo.plivoincomingcall.dagger2.ViewComponent;
import com.plivo.plivoincomingcall.dagger2.ViewContext;
import com.plivo.plivoincomingcall.model.Call;
import com.plivo.plivoincomingcall.model.Contact;
import com.plivo.plivoincomingcall.model.internal.TelephonyCall;
import com.plivo.plivoincomingcall.screens.dial.calls.IncomingCallFragment;
import com.plivo.plivoincomingcall.screens.dial.calls.MoreCallsFragment;
import com.plivo.plivoincomingcall.screens.dial.calls.OngoingCallFragment;
import com.plivo.plivoincomingcall.screens.dial.tabs.DialFragment;
import com.plivo.plivoincomingcall.screens.login.LoginActivity;
import com.plivo.plivoincomingcall.utils.AlarmUtils;
import com.plivo.plivoincomingcall.utils.AlertUtils;
import com.plivo.plivoincomingcall.utils.Constants;
import com.plivo.plivoincomingcall.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.telephony.TelephonyManager;

import static com.plivo.plivoincomingcall.utils.Constants.REQUEST_CODE_DIAL_PERMISSIONS;

public class DialActivity extends BaseActivity {
    private static final String TAG = DialActivity.class.getSimpleName();

    @BindView(R.id.dial_parent)
    ConstraintLayout parentLayout;

    @BindView(R.id.fragment_container)
    ViewGroup fragmentContainer;

    @BindView(R.id.more_calls_fragment_container)
    FrameLayout moreCallsFragmentContainer;

    @BindView(R.id.black_overlay)
    View blackOverlay;

    @Inject
    IncomingCallFragment incomingCallFragment;

    @Inject
    OngoingCallFragment ongoingCallFragment;

    @Inject
    MoreCallsFragment moreCallsFragment;

    @Inject
    DialFragment dialFragment;

    @Inject
    AlertUtils alertUtils;

    @Inject
    AlarmUtils alarmUtils;

    @Inject
    NetworkUtils networkUtils;

    private DialViewModel viewModel;

    private ViewComponent viewComponent;

    private MenuItem logoutMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial);
        ButterKnife.bind(this);
        viewModel = ViewModelProviders.of(this).get(DialViewModel.class);
        setViewComponent(DaggerViewComponent.builder().viewContext(new ViewContext(this)).build());
        getViewComponent().inject(this);
        EventBus.getDefault().register(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        enableKeyGuard();

        currentCall = viewModel.getCurrentCall();
        if (!alertUtils.checkAllPermissionsGranted()) {
            alertUtils.requestDialPermissionsRequired(REQUEST_CODE_DIAL_PERMISSIONS);
        } else {
            setupView();
        }
        showLogout(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel.callStackObserver().observe(this, call -> {
            showFullScreen(false);
            updateUi(call);
        });

        if (getIntent().getBooleanExtra(Constants.INCOMING_CALL_FROM_PUSH, false)) {
            showFullScreen(true);
        } else {
            blackOverlay.setVisibility(View.GONE);
            updateUi(viewModel.getCurrentCall());
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_DIAL_PERMISSIONS:
                if (grantResults != null && grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupView();
                }
                break;
        }
    }

    private void enableKeyGuard() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    private void showFullScreen(boolean show) {
        if (show) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            getSupportActionBar().hide();
            blackOverlay.setVisibility(View.VISIBLE);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            getSupportActionBar().show();
            blackOverlay.setVisibility(View.GONE);
        }
    }

    private void setupView() {
        showDial();
        showLogout(true);
        updateUi(currentCall);
    }

    public void showLogout(boolean show) {
        if (logoutMenu != null) {
            logoutMenu.setVisible(show);
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dial_menu, menu);
        logoutMenu = menu.findItem(R.id.logout);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Call c = viewModel.getCurrentCall();
        if (c == null || c.isIdle()) {
            showLogout(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        if (!networkUtils.isNetworkAvailable()) {
            alertUtils.showToast(getString(R.string.network_unavailable));
            return;
        }

        viewModel.logoutObserver().observe(this, success -> {
            if (success) {
                doLogout();
            }
        });

        if (viewModel.isUserLoggedIn()) {
            viewModel.logout();
        } else {
            doLogout();
        }
    }

    private void doLogout() {
        alarmUtils.cancelRepeatingAlarm();
        showLogin();
    }

    private void showLogin() {
        startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    private void updateUi(Call call) {
        Log.d(TAG, "updateUi " + call);
        if (call == null) return;

        currentCall = call;
        Log.d(TAG, currentCall.getState().name());

        if (call.isIncoming() && call.isRinging()) {
            // incoming
            if (viewModel.isCarrierCallInProgress()) {
                viewModel.terminate();
                showIdle();
                return;
            }

            showIncoming();
            moreCallsFragment.showOtherCallsList(false);
        } else if (call.isExpired()) {
            viewModel.terminate();
            showIdle();
            return;
        } else {
            switch (call.getState()) {
                case IDLE:
                case HANGUP:
                case REJECTED:
                    showIdle();
                    break;

                default:
                    showOngoing();
                    moreCallsFragment.showOtherCallsList(true);
                    break;
            }
        }

        if (viewModel.getAvailableCalls().size() > 1) {
            showFragment(moreCallsFragmentContainer, moreCallsFragment);
        }
    }

    private void showOngoing() {
        showFragment(fragmentContainer, ongoingCallFragment);
        showLogout(false);
    }

    private void showIncoming() {
        showFragment(fragmentContainer, incomingCallFragment);
        showLogout(false);
    }

    private void showIdle() {
        removeCurrentCallFragment();
        showLogout(true);
        setTitle("Call " + Call.STATE.IDLE);
        showDial();
    }

    private void showDial() {
        showFragment(fragmentContainer, dialFragment);
    }

    // from contacts
    public void onClickContact(Contact selected) {
        if (!networkUtils.isNetworkAvailable()) {
            alertUtils.showToast("Network Unavailable");
            return;
        }

        viewModel.call(Call.newCall(selected));
        showOngoing();
    }

    // from call log
    public void onClickCallLog(Call call) {
        if (!networkUtils.isNetworkAvailable()) {
            alertUtils.showToast("Network Unavailable");
            return;
        }

        viewModel.call(call);
        showOngoing();
    }

    // from more calls page
    public void onSelectCall(Call selected) {
        updateUi(selected);
    }

    public void onMoreFragmentLoaded() {
        if (currentCall == null) return;

        moreCallsFragment.showOtherCallsList(!(currentCall.isIncoming() && currentCall.isRinging()));
    }

    // from ongoingCall
    public void onClickDialer(boolean isShown) {
        Log.d(TAG, "onClickDialer " + isShown);
        moreCallsFragment.showOtherCallsList(!isShown);
    }

    // telephony services
    @Subscribe
    public void onTelephonyCallStateChanged(TelephonyCall telephonyCall) {
        Log.d(TAG, "onTelephonyCallStateChanged " + telephonyCall.getState() + " " + viewModel.getCurrentCall());
        switch (telephonyCall.getState()) {
            case TelephonyManager.CALL_STATE_RINGING:
            case TelephonyManager.CALL_STATE_IDLE:
                viewModel.unHold(); // current call unhold
                ongoingCallFragment.showCarrierInProgressOverlay(false);
                ongoingCallFragment.updateUi(viewModel.getCurrentCall());
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (viewModel.getCurrentCall() != null) {
                    viewModel.hold(); // current call on hold
                    ongoingCallFragment.showCarrierInProgressOverlay(true);
                    ongoingCallFragment.updateUi(viewModel.getCurrentCall());
                    alertUtils.showToast(viewModel.getCurrentCall().getContact().getName() + " on hold");
                }
                break;
        }

        BaseFragment currentFragment = getCurrentFragment();
        if (currentFragment != null) {
            currentFragment.updateUi(viewModel.getCurrentCall());
        }
    }

    public ViewComponent getViewComponent() {
        return viewComponent;
    }

    public void setViewComponent(ViewComponent viewComponent) {
        this.viewComponent = viewComponent;
    }
}
