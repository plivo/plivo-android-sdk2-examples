package com.plivo.plivoaddressbook.layer.impl;

import android.text.TextUtils;

import com.plivo.plivoaddressbook.model.Contact;
import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.EventListener;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;
import com.plivo.endpoint.backend.plivo;
import com.plivo.plivoaddressbook.BuildConfig;
import com.plivo.plivoaddressbook.layer.plivo.PlivoBackend;
import com.plivo.plivoaddressbook.layer.plivo.PlivoBackendListener;
import com.plivo.plivoaddressbook.layer.plivo.PlivoCallStack;
import com.plivo.plivoaddressbook.model.Call;
import com.plivo.plivoaddressbook.model.User;
import com.plivo.plivoaddressbook.utils.ContactUtils;
import com.plivo.plivoaddressbook.utils.PreferencesUtils;

/**
 * Singleton class that interacts with the backend plivo (Android SDK & Jar)
 */
public class PlivoSDKImpl extends PlivoBackend implements EventListener {
    private static final String TAG = PlivoSDKImpl.class.getSimpleName();


    private Endpoint endpoint;

    public PlivoSDKImpl(PlivoCallStack callObj, ContactUtils contactUtils) {
        super(callObj, contactUtils);
    }

    public boolean isLoggedIn() {
        return endpoint().getRegistered();
    }

    public void keepAlive(PlivoBackendListener.LoginListener listener) {
        // todo: enable keepAlive / loginAgain, once fixed on SDK
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

    public boolean outCall(String number) {
        try {
            return createOutgoing().call(number);
        } catch (Endpoint.EndpointNotRegisteredException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void answer() {
        incoming().answer();
        getCurrentCall().setState(Call.STATE.ANSWERED);
        notifyCallStackChange(getCurrentCall());
    }

    public void reject() {
        incoming().reject();
    }

    public void hangUp() {
        if (getCurrentCall().isIncoming())
            incoming().hangup();
        else
            outgoing().hangup();
    }

    public boolean mute() {
        super.mute();
        if (getCurrentCall().isIncoming())
            return incoming().mute();
        else
            return outgoing().mute();
    }

    public boolean unMute() {
        super.unMute();
        if (getCurrentCall().isIncoming())
            return incoming().unmute();
        else
            return outgoing().unmute();
    }

    public void hold() {
        super.hold();
        // todo: line hold
    }

    public void unHold() {
        super.unHold();
        // todo: line unhold
    }

    public boolean sendDigit(String digit) {
        if (getCurrentCall().isIncoming()) {
            return incoming().sendDigits(digit);
        } else {
            return outgoing().sendDigits(digit);
        }
    }


    // objects
    private Endpoint endpoint() {
        return endpoint != null? endpoint:
                (endpoint = Endpoint.newInstance(BuildConfig.DEBUG, this));
    }

    private Outgoing createOutgoing() throws Endpoint.EndpointNotRegisteredException {
        Outgoing outgoing = endpoint().createOutgoingCall();
        return outgoing;
    }

    private Incoming incoming() {
        return (Incoming) getCurrentCall().getData();
    }

    private Outgoing outgoing() {
        return (Outgoing) getCurrentCall().getData();
    }

    private String from(String fromContact, String fromSip) {
        String from = TextUtils.isEmpty(fromContact)?
                TextUtils.isEmpty(fromSip)? "" : fromSip:
                fromContact;
        return from.contains("\"") ?
                from.substring(from.indexOf("\"")+1, from.lastIndexOf("\"")):
                from;

    }

    private String to(String toSip) {
        return TextUtils.isEmpty(toSip) ? "" :
                toSip.substring(toSip.indexOf(":")+1, toSip.indexOf("@"));
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
                .setType(Call.TYPE.INCOMING)
                .setState(Call.STATE.RINGING)
                .setContact(contactUtils.getContact(from(incoming.getFromContact(), incoming.getFromSip())))
                .setData(incoming)
                .build();
        addToCallStack(inCall);
    }

    @Override
    public void onIncomingCallHangup(Incoming incoming) {
        Call call = getCall(incoming.getCallId());
        if (call != null) {
            call.setState(Call.STATE.HANGUP);
            removeFromCallStack(call);
        }
    }

    @Override
    public void onIncomingCallRejected(Incoming incoming) {
        Call call = getCall(incoming.getCallId());
        if (call != null) {
            call.setState(Call.STATE.REJECTED);
            removeFromCallStack(call);
        }
    }

    @Override
    public void onOutgoingCall(Outgoing outgoing) {
        // create call obj
        Call outCall = new Call.Builder()
                .setId(outgoing.getCallId())
                .setType(Call.TYPE.OUTGOING)
                .setState(Call.STATE.RINGING)
                .setContact(contactUtils.getContact(to(outgoing.getToContact())))
                .setData(outgoing)
                .build();
        addToCallStack(outCall);
    }

    @Override
    public void onOutgoingCallAnswered(Outgoing outgoing) {
        Call call = getCall(outgoing.getCallId());
        if (call != null) call.setState(Call.STATE.ANSWERED);
        notifyCallStackChange(call);
    }

    @Override
    public void onOutgoingCallRejected(Outgoing outgoing) {
        Call call = getCall(outgoing.getCallId());
        if (call != null) {
            call.setState(Call.STATE.REJECTED);
            removeFromCallStack(call);
        }
    }

    @Override
    public void onOutgoingCallHangup(Outgoing outgoing) {
        Call call = getCall(outgoing.getCallId());
        if (call != null) {
            call.setState(Call.STATE.HANGUP);
            removeFromCallStack(call);
        }
    }

    @Override
    public void onOutgoingCallInvalid(Outgoing outgoing) {
        notifyCallStackChange(new Call.Builder().setState(Call.STATE.INVALID).build());
    }
}
