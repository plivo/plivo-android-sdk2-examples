package com.plivo.plivoincomingcall.layer.impl;

import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.EventListener;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;
import com.plivo.endpoint.backend.plivo;
import com.plivo.plivoincomingcall.BuildConfig;
import com.plivo.plivoincomingcall.layer.plivo.PlivoBackend;
import com.plivo.plivoincomingcall.layer.plivo.PlivoBackendListener;
import com.plivo.plivoincomingcall.layer.plivo.PlivoCall;
import com.plivo.plivoincomingcall.model.Call;
import com.plivo.plivoincomingcall.model.User;
import com.plivo.plivoincomingcall.utils.PreferencesUtils;

/**
 * Singleton class that interacts with the backend plivo (Android SDK & Jar)
 */
public class PlivoSDKImpl extends PlivoBackend implements EventListener {
    private static final String TAG = PlivoSDKImpl.class.getSimpleName();


    private Endpoint endpoint;

    public PlivoSDKImpl(PlivoCall callObj) {
        super(callObj);
    }

    public boolean isLoggedIn() {
        return endpoint().getRegistered();
    }

    public void keepAlive(PlivoBackendListener.LoginListener listener) {
        // todo: currently keepAlive() is not working on SDK
//        this.loginListener = listener;
//        endpoint().keepAlive();

//        loginAgain(listener);

        listener.onLogin(false);
    }

    public void loginAgain(PlivoBackendListener.LoginListener listener) {
        super.loginAgain(listener);
        plivo.LoginAgain();
    }

    public void login(User user, PlivoBackendListener.LoginListener listener) {
        super.login(user, listener);
        endpoint().setRegTimeout(PreferencesUtils.LOGIN_TIMEOUT);
        endpoint().login(user.getUsername(), user.getPassword());
    }

    public boolean logout(PlivoBackendListener.LogoutListener listener) {
        super.logout(listener);
        return endpoint().logout();
    }

    public void answer() {
        incoming().answer();
        getCurrentCall().setState(PlivoCall.CALL_STATE.ANSWERED);
        notifyIncomingCall();
    }

    public void reject() {
        incoming().reject();
    }

    public void hangUp() {
            incoming().hangup();
    }

    public boolean mute() {
            return incoming().mute();
    }

    public boolean unMute() {
            return incoming().mute();
    }

    public boolean sendDigit(String digit) {
            return incoming().sendDigits(digit);
    }

    // objects
    private Endpoint endpoint() {
        return endpoint != null? endpoint:
                (endpoint = Endpoint.newInstance(BuildConfig.DEBUG, this));
    }

    private Incoming incoming() {
        return (Incoming) getCurrentCall().getData();
    }

    // Endpoint listeners
    @Override
    public void onLogin() {
        notifyLogin(true);
    }

    @Override
    public void onLoginFailed() {
        notifyLogin(false);
    }

    @Override
    public void onLogout() {
        notifyLogout();
    }

    @Override
    public void onIncomingDigitNotification(String digit) {
        notifyDTMF(digit);
    }

    @Override
    public void onIncomingCall(Incoming incoming) {
        // create call obj
        Call inCall = new Call.Builder()
                .setId(incoming.getCallId())
                .setType(PlivoCall.CALL_TYPE.INCOMING)
                .setState(PlivoCall.CALL_STATE.RINGING)
                .setFromContact(incoming.getFromContact())
                .setFromSip(incoming.getFromSip())
                .setToContact(incoming.getToContact())
                .setToSip(incoming.getToSip())
                .setData(incoming)
                .build();
        setCurrentCall(inCall);
        notifyIncomingCall();
    }

    @Override
    public void onIncomingCallHangup(Incoming incoming) {
        getCurrentCall().setState(PlivoCall.CALL_STATE.HANGUP);
        notifyIncomingCall();
    }

    @Override
    public void onIncomingCallRejected(Incoming incoming) {
        getCurrentCall().setState(PlivoCall.CALL_STATE.REJECTED);
        notifyIncomingCall();
    }

    @Override
    public void onOutgoingCall(Outgoing outgoing) {}

    @Override
    public void onOutgoingCallAnswered(Outgoing outgoing) {}

    @Override
    public void onOutgoingCallRejected(Outgoing outgoing) {}

    @Override
    public void onOutgoingCallHangup(Outgoing outgoing) {}

    @Override
    public void onOutgoingCallInvalid(Outgoing outgoing) {}
}
