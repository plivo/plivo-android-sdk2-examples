package com.plivo.plivoregistration.plivo.layer;

import android.util.Log;

import com.plivo.plivoregistration.model.User;

import java.util.concurrent.TimeUnit;

public class PlivoLogin extends PlivoBackend {
    private static final String TAG = PlivoLogin.class.getSimpleName();

    private static final int LOGIN_TIMEOUT = (int) TimeUnit.MINUTES.toSeconds(10);

    private BackendLoginListener listener;

    public interface BackendLoginListener {
        void onLogin(boolean success);
        void onLogout();
    }

    public void login(User user, BackendLoginListener listener) {
        this.listener = listener;
        plivoEndpoint().setRegTimeout(LOGIN_TIMEOUT);
        plivoEndpoint().login(user.getUsername(), user.getPassword());
    }

    public boolean logout() {
        return plivoEndpoint().logout();
    }

    @Override
    public void onLogin() {
        super.onLogin();
        Log.d(TAG, "onLogin LOGIN SUCCESS");
        if (listener != null) listener.onLogin(true);
    }

    @Override
    public void onLoginFailed() {
        super.onLoginFailed();
        Log.e(TAG, "onLogin LOGIN FAILED");
        if (listener != null) listener.onLogin(false);
    }

    @Override
    public void onLogout() {
        super.onLogout();
        Log.d(TAG, "onLogout SUCCESS");
        if (listener != null) listener.onLogout();
    }
}
