package com.plivo.plivoincomingcall.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.plivo.plivoincomingcall.App;
import com.plivo.plivoincomingcall.layer.plivo.PlivoBackend;
import com.plivo.plivoincomingcall.model.User;
import com.plivo.plivoincomingcall.screens.login.LoginActivity;
import com.plivo.plivoincomingcall.utils.Constants;
import com.plivo.plivoincomingcall.utils.NotificationUtils;
import com.plivo.plivoincomingcall.utils.PreferencesUtils;

import javax.inject.Inject;

import androidx.annotation.Nullable;

/**
 * This is the component responsible for listening to incoming callbacks in the background
 */
public class PlivoBackgroundService extends Service {
    private static final String TAG = PlivoBackgroundService.class.getSimpleName();

    public static final String COMMAND = "cmd";
    public static final String START = "start";
    public static final String STOP = "stop";

    @Inject
    PlivoBackend backend;

    @Inject
    NotificationUtils notificationUtils;

    @Inject
    PreferencesUtils preferencesUtils;

    private static boolean isRunning;

    public static boolean isRunning() {
        return isRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((App) getApplication()).getAppComponent().inject(this);
        notificationUtils.createNotification(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = intent.getStringExtra(COMMAND).equals(START);

        handleCommand(intent.getStringExtra(COMMAND));

        return START_NOT_STICKY;
    }

    private void handleCommand(String command) {
        switch (command) {

            case START:
                observeLogin();
                observeIncomingCall();
                break;

            case STOP:
                stopForeground(true);
                stopSelf();
                break;
        }
    }

    // model
    private void observeLogin() {
        if (preferencesUtils.isLoginExpired()) {
            backend.keepAlive(success -> {
                if (!success) {
                    // force login
                    User loggedInUser = preferencesUtils.getUser();
                    if (loggedInUser != null) {
                        backend.login(loggedInUser, onLoginSuccess -> {});
                    }
                }
            });
        }
    }

    private void observeIncomingCall() {
        backend.setIncomingCallListener(state -> {
            startActivity(new Intent(this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Constants.EXTRA_INCOMING_CALL, true)
            );
        });
    }

    @Nullable
    @android.support.annotation.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
