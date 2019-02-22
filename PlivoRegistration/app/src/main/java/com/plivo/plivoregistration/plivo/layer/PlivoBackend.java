package com.plivo.plivoregistration.plivo.layer;

import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.EventListener;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;
import com.plivo.plivoregistration.BuildConfig;
import com.plivo.plivoregistration.model.User;

import java.util.concurrent.TimeUnit;

/**
 * Singleton class that interacts with the backend plivo (Android SDK & Jar)
 */
public class PlivoBackend implements EventListener {
    private static final String TAG = PlivoBackend.class.getSimpleName();
    private static final int LOGIN_TIMEOUT = (int) TimeUnit.MINUTES.toSeconds(10);

    private Endpoint endpoint;

    private PlivoBackendListener.LoginListener loginListener;
    private PlivoBackendListener.LogoutListener logoutListener;

    protected Endpoint plivoEndpoint() {
        return endpoint != null? endpoint:
                (endpoint = Endpoint.newInstance(BuildConfig.DEBUG, this));
    }

    public void login(User user, PlivoBackendListener.LoginListener listener) {
        this.loginListener = listener;
        plivoEndpoint().setRegTimeout(LOGIN_TIMEOUT);
        plivoEndpoint().login(user.getUsername(), user.getPassword());
    }

    public boolean logout(PlivoBackendListener.LogoutListener listener) {
        this.logoutListener = listener;
        return plivoEndpoint().logout();
    }

    // Endpoint listeners
    @Override
    public void onLogin() {
        if (loginListener != null) loginListener.onLogin(true);
    }

    @Override
    public void onLogout() {
        if (logoutListener != null) logoutListener.logout();
    }

    @Override
    public void onLoginFailed() {
        if (loginListener != null) loginListener.onLogin(false);
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
    }

    @Override
    public void onOutgoingCallAnswered(Outgoing outgoing) {

    }

    @Override
    public void onOutgoingCallRejected(Outgoing outgoing) {

    }

    @Override
    public void onOutgoingCallHangup(Outgoing outgoing) {

    }

    @Override
    public void onOutgoingCallInvalid(Outgoing outgoing) {

    }
}
