package com.plivo.plivoaddressbook.layer.plivo;

import android.util.Log;

import com.plivo.endpoint.Endpoint;
import com.plivo.plivoaddressbook.model.Call;
import com.plivo.plivoaddressbook.model.User;
import com.plivo.plivoaddressbook.utils.ContactUtils;

import java.util.List;
import java.util.Map;

/**
 * Singleton class that interacts with the backend plivo (Android SDK & Jar)
 */
public abstract class PlivoBackend {
    private static final String TAG = PlivoBackend.class.getSimpleName();

    private PlivoBackendListener.LoginListener loginListener;
    private PlivoBackendListener.LogoutListener logoutListener;
    private PlivoBackendListener.CallStackListener callStackListener;
    private PlivoBackendListener.DTMFListener incomingDTMFListener;

    private PlivoCallStack callStack;
    protected ContactUtils contactUtils;

    public PlivoBackend(PlivoCallStack callObj, ContactUtils contactUtils) {
        this.callStack = callObj;
        this.contactUtils = contactUtils;
    }

    public void setCallStackListener(PlivoBackendListener.CallStackListener listener) {
        callStackListener = listener;
    }

    public void setIncomingDTMFListener(PlivoBackendListener.DTMFListener listener) {
        incomingDTMFListener = listener;
    }

    public void setCurrentCall(Call currentCall) {
        Call c = currentCall;
        if (c != null) {
            Log.d(TAG, "setCurrentCall " + c.getId() + " " + c.getType() + " " + c.getState());
        }
        callStack.setCurrentCall(currentCall);
    }

    public Call getCurrentCall() {
        Call c = callStack.getCurrentCall();
        if (c != null) {
            Log.d(TAG, "getCurrentCall " + c.getId() + " " + c.getType() + " " + c.getState());
        }
        return callStack.getCurrentCall();
    }

    public List<Call> getAvailableCalls() {
        return callStack.getCallStack();
    }

    public abstract boolean isLoggedIn();

    public abstract void keepAlive(PlivoBackendListener.LoginListener listener);

    protected void loginAgain(PlivoBackendListener.LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public boolean login(User user, PlivoBackendListener.LoginListener listener) {
        this.loginListener = listener;
        return false;
    }

    public boolean logout(PlivoBackendListener.LogoutListener listener) {
        this.logoutListener = listener;
        return false;
    }

    public abstract boolean outCall(String number);

    public abstract void answer();

    public abstract void reject();

    public abstract void hangUp();

    public boolean mute() {
        getCurrentCall().setMute(true); // update ui model call
        return false;
    }

    public boolean unMute() {
        getCurrentCall().setMute(false); // update ui model call
        return false;
    }

    public boolean hold() {
        getCurrentCall().setHold(true); // update ui model call
        return false;
    }

    public boolean unHold() {
        getCurrentCall().setHold(false); // update ui model call
        return false;
    }

    public abstract boolean sendDigit(String digit);

    protected Call getCall(String callId) {
        return callStack.getCall(callId);
    }

    public void terminateCall() {}

    // notify when call stack entry changes
    protected void notifyCallStackChange(Call c) {
        if (c == null) return;

        notifyWhenCurrent(c);
    }

    // notify only when state changes for current call,
    // else state is preserved on the stack, but not notified
    private void notifyWhenCurrent(Call call) {
        if (call == null) return;

        if (getCurrentCall() == null ||
                getCurrentCall().getId().equals(call.getId())) {
            notifyDirect(call);
        } else {
            notifyDirect(getCurrentCall());
        }
    }

    // notify direct for any changes out of call stack
    private void notifyDirect(Call call) {
        if (callStackListener != null) {
            callStackListener.onCallChanged(call);
        }
    }

    protected void notifyDTMF(String digit) {
        if (incomingDTMFListener != null) incomingDTMFListener.onDTMFReceived(digit);
    }

    protected void notifyLogin(boolean success) {
        if (loginListener != null) loginListener.onLogin(success);
    }

    public void registerFCMToken(String token) {}

    public void relayPushNotification(Map<String, String> notification) {}

    protected void notifyLogout() {
        if (logoutListener != null) logoutListener.logout();
    }

    protected void addToCallStack(Call c) {
        if (callStack.addToCallStack(c)) {
            notifyCallStackChange(c);
        }
    }

    protected void removeFromCallStack(Call c) {
        if (callStack.removeFromCallStack(c)) {
            notifyCallStackChange(c);
        }
    }

    public void clearCallStack() {
        callStack.clearCallStack();
    }

    // Exceptions

}
