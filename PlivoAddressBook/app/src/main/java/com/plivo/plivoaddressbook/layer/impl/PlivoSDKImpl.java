package com.plivo.plivoaddressbook.layer.impl;

import android.os.Handler;
import android.text.TextUtils;

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

import java.util.HashMap;
import java.util.Map;

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
        endpoint().keepAlive();
    }

    public void loginAgain(PlivoBackendListener.LoginListener listener) {
        super.loginAgain(listener);
        plivo.LoginAgain();
    }

    public boolean login(User user, PlivoBackendListener.LoginListener listener) {
        super.login(user, listener);
        return endpoint().login(user.getUsername(), user.getPassword());
//        return endpoint().login(user.getUsername(), user.getPassword(), user.getDeviceToken());
    }

    public boolean logout(PlivoBackendListener.LogoutListener listener) {
        super.logout(listener);
        return endpoint().logout();
    }

    public void relayPushNotification(Map<String, String> notification) {
        endpoint().relayVoipPushNotification(notification);
    }

    public boolean outCall(String number) {
        return createOutgoing().call(number);
    }

    public void answer() {
        if (incoming().answer()) {
            getCurrentCall().setState(Call.STATE.ANSWERED);
            notifyCallStackChange(getCurrentCall());
            handler.removeCallbacks(ringingTimeoutRunnable);
        }
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

    public boolean hold() {
        super.hold();
        if (getCurrentCall().isIncoming())
            return incoming().hold();
        else
            return outgoing().hold();
    }

    public boolean unHold() {
        super.unHold();
        if (getCurrentCall().isIncoming())
            return incoming().unhold();
        else
            return outgoing().unhold();
    }

    public boolean sendDigit(String digit) {
        Call call = getCurrentCall();
        if (call != null && !call.isActive()) return false;

        if (call.isIncoming()) {
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

    private Outgoing createOutgoing() {
        return endpoint().createOutgoingCall();
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
        if (TextUtils.isEmpty(digit)) return;
        notifyDTMF(digit.equals("-6") ? "*" : digit.equals("-13") ? "#" : digit);
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
        handler.postDelayed(ringingTimeoutRunnable, Call.CALL_RINGING_TIMEOUT);
    }

    @Override
    public void onIncomingCallHangup(Incoming incoming) {
        Call call = getCall(incoming.getCallId());
        if (call != null) {
            call.setState(Call.STATE.HANGUP);
            removeFromCallStack(call);
        }
        handler.removeCallbacks(ringingTimeoutRunnable);
    }

    @Override
    public void onIncomingCallRejected(Incoming incoming) {
        Call call = getCall(incoming.getCallId());
        if (call != null) {
            call.setState(Call.STATE.REJECTED);
            removeFromCallStack(call);
        }
        handler.removeCallbacks(ringingTimeoutRunnable);
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
        handler.postDelayed(ringingTimeoutRunnable, Call.CALL_RINGING_TIMEOUT);
    }

    private Handler handler = new Handler();

    private Runnable ringingTimeoutRunnable = () -> terminateCall();

    @Override
    public void terminateCall() {
        Call call = getCurrentCall();
        if (call != null) {
            if (call.isIncoming()) {
                reject();
            } else {
                hangUp();
            }
            removeFromCallStack(call);
        }
    }

    @Override
    public void onOutgoingCallAnswered(Outgoing outgoing) {
        Call call = getCall(outgoing.getCallId());
        if (call != null) call.setState(Call.STATE.ANSWERED);
        handler.removeCallbacks(ringingTimeoutRunnable);
        notifyCallStackChange(call);
    }

    @Override
    public void onOutgoingCallRejected(Outgoing outgoing) {
        Call call = getCall(outgoing.getCallId());
        if (call != null) {
            call.setState(Call.STATE.REJECTED);
            removeFromCallStack(call);
        }
        handler.removeCallbacks(ringingTimeoutRunnable);
    }

    @Override
    public void onOutgoingCallHangup(Outgoing outgoing) {
        Call call = getCall(outgoing.getCallId());
        if (call != null) {
            call.setState(Call.STATE.HANGUP);
            removeFromCallStack(call);
        }
        handler.removeCallbacks(ringingTimeoutRunnable);
    }

    @Override
    public void onOutgoingCallInvalid(Outgoing outgoing) {
        notifyCallStackChange(new Call.Builder().setState(Call.STATE.INVALID).build());
        handler.removeCallbacks(ringingTimeoutRunnable);
    }
}
