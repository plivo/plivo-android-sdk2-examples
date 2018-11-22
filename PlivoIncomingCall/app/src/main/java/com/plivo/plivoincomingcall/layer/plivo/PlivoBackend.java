package com.plivo.plivoincomingcall.layer.plivo;

import com.plivo.plivoincomingcall.model.Call;
import com.plivo.plivoincomingcall.model.User;

/**
 * Singleton class that interacts with the backend plivo (Android SDK & aar)
 */
public abstract class PlivoBackend {
    private static final String TAG = PlivoBackend.class.getSimpleName();

    private PlivoBackendListener.LoginListener loginListener;
    private PlivoBackendListener.LogoutListener logoutListener;
    private PlivoBackendListener.CallListener incomingCallListener;
    private PlivoBackendListener.DTMFListener incomingDTMFListener;

    private PlivoCall call;

    public PlivoBackend(PlivoCall callObj) {
        call = callObj;
    }

    public void setIncomingCallListener(PlivoBackendListener.CallListener listener) {
        incomingCallListener = listener;
    }

    public void setIncomingDTMFListener(PlivoBackendListener.DTMFListener listener) {
        incomingDTMFListener = listener;
    }

    public void setCurrentCall(Call currentCall) {
        call.setCurrentCall(currentCall);
    }

    public Call getCurrentCall() {
        return call.getCurrentCall();
    }

    public abstract boolean isLoggedIn();

    public abstract void keepAlive(PlivoBackendListener.LoginListener listener);

    protected void loginAgain(PlivoBackendListener.LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public void login(User user, PlivoBackendListener.LoginListener listener) {
        this.loginListener = listener;
    }

    public boolean logout(PlivoBackendListener.LogoutListener listener) {
        this.logoutListener = listener;
        return false;
    }

    public abstract void answer();

    public abstract void reject();

    public abstract void hangUp();

    public abstract boolean mute();

    public abstract boolean unMute();

    public abstract boolean sendDigit(String digit);

    protected void notifyIncomingCall() {
        if (incomingCallListener != null) {
            incomingCallListener.onCall(getCurrentCall());
        }
    }

    protected void notifyDTMF(String digit) {
        if (incomingDTMFListener != null) incomingDTMFListener.onDTMFReceived(digit);
    }

    protected void notifyLogin(boolean success) {
        if (loginListener != null) loginListener.onLogin(success);
    }

    protected void notifyLogout() {
        if (logoutListener != null) logoutListener.logout();
    }

}
