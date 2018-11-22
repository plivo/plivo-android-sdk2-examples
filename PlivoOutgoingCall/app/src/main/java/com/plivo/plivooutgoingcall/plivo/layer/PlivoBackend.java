package com.plivo.plivooutgoingcall.plivo.layer;

import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.Endpoint.EndpointNotRegisteredException;
import com.plivo.endpoint.EventListener;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;
import com.plivo.plivooutgoingcall.BuildConfig;
import com.plivo.plivooutgoingcall.model.User;

import java.util.concurrent.TimeUnit;

/**
 * Singleton class that interacts with the backend plivo (Android SDK & Jar)
 */
public class PlivoBackend implements EventListener {
    private static final String TAG = PlivoBackend.class.getSimpleName();
    private static final int LOGIN_TIMEOUT = (int) TimeUnit.MINUTES.toSeconds(10);

    private Endpoint endpoint;
    private Outgoing outgoing;

    private PlivoBackendListener.LoginListener loginListener;
    private PlivoBackendListener.LogoutListener logoutListener;
    private PlivoBackendListener.CallListener callListener;

    private PlivoCallState.OUT_CALL_STATE outCallState;

    public void login(User user, PlivoBackendListener.LoginListener listener) {
        this.loginListener = listener;
        plivoEndpoint().setRegTimeout(LOGIN_TIMEOUT);
        plivoEndpoint().login(user.getUsername(), user.getPassword());
    }

    public boolean logout(PlivoBackendListener.LogoutListener listener) {
        this.logoutListener = listener;
        return plivoEndpoint().logout();
    }

    public boolean outCall(String number, PlivoBackendListener.CallListener listener) throws EndpointNotRegisteredException {
        this.callListener = listener;
        return outgoing().call(number);
    }

    public void hangUp(PlivoBackendListener.CallListener listener) throws Endpoint.EndpointNotRegisteredException {
        this.callListener = listener;
        outgoing().hangup();
    }

    public void mute() throws EndpointNotRegisteredException {
        outgoing().mute();
    }

    public void unMute() throws EndpointNotRegisteredException {
        outgoing().unmute();
    }


    // objects
    protected Endpoint plivoEndpoint() {
        return endpoint != null? endpoint:
                (endpoint = Endpoint.newInstance(BuildConfig.DEBUG, this));
    }

    private Outgoing outgoing() throws EndpointNotRegisteredException {
        if (outgoing == null) {
            outgoing = plivoEndpoint().createOutgoingCall();
        }

        return outgoing;
    }

    private void notifyCallState() {
        if (callListener != null) callListener.onCall(outCallState);
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
        this.outgoing = outgoing;
        outCallState = PlivoCallState.OUT_CALL_STATE.RINGING;
        notifyCallState();
    }

    @Override
    public void onOutgoingCallAnswered(Outgoing outgoing) {
        this.outgoing = outgoing;
        outCallState = PlivoCallState.OUT_CALL_STATE.ANSWERED;
        notifyCallState();
    }

    @Override
    public void onOutgoingCallRejected(Outgoing outgoing) {
        this.outgoing = outgoing;
        outCallState = PlivoCallState.OUT_CALL_STATE.REJECTED;
        notifyCallState();
    }

    @Override
    public void onOutgoingCallHangup(Outgoing outgoing) {
        this.outgoing = outgoing;
        outCallState = PlivoCallState.OUT_CALL_STATE.HANGUP;
        notifyCallState();
    }

    @Override
    public void onOutgoingCallInvalid(Outgoing outgoing) {
        this.outgoing = outgoing;
        outCallState = PlivoCallState.OUT_CALL_STATE.INVALID;
        notifyCallState();
    }
}
