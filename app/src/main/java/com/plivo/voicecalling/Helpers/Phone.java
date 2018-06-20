package com.plivo.voicecalling.Helpers;

import android.util.Log;

import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.EventListener;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;

public class Phone implements EventListener
{
    private static final Phone ourInstance = new Phone();

    public Endpoint endpoint;

    private static EndPointListner endPointListener1;

    public static Phone getInstance(EndPointListner endPointListener) {

        endPointListener1 = endPointListener;
        return ourInstance;
    }

    private Phone() {
        endpoint = new Endpoint(true, this);
    }

    public boolean login(String username, String password) {
       return endpoint.login(username,password);
    }

    public Outgoing createOutgoingCall() {
       return endpoint.createOutgoingCall();
    }

    public void onLogin() {
        Log.d("Phone - Singleton", "Logging in");
        endPointListener1.onLogin();
    }

    public void onLogout() {
        Log.d("Phone ", "Logging out");
        endPointListener1.onLogout();
    }

    public void onLoginFailed() {
        Log.d("Phone", "Login failed");
        endPointListener1.onLoginFailed();
    }

    public void onIncomingCall(Incoming incoming) {
        Log.d("Phone ", "Incoming Call");
        endPointListener1.onIncomingCall(incoming);
    }

    public void onIncomingCallHangup(Incoming incoming) {
        Log.d("Phone", "onIncomingCallHangup");
        endPointListener1.onIncomingCallHangup(incoming);
    }

    public void onIncomingCallRejected(Incoming incoming) {
        Log.d("Phone", "onIncomingCallRejected");
        endPointListener1.onIncomingCallRejected(incoming);
    }

    public void onOutgoingCall(Outgoing outgoing) {
        Log.d("Phone", "onOutgoingCall");
        endPointListener1.onOutgoingCall(outgoing);
    }

    public void onOutgoingCallAnswered(Outgoing outgoing) {
        Log.d("Phone", "onOutgoingCallAnswered");
        endPointListener1.onOutgoingCallAnswered(outgoing);

    }

    public void onOutgoingCallRejected(Outgoing outgoing) {
        Log.d("Phone", "onOutgoingCallRejected");
        endPointListener1.onOutgoingCallRejected(outgoing);
    }

    public void onOutgoingCallHangup(Outgoing outgoing) {
        Log.d("Phone", "onOutgoingCallHangup");
        endPointListener1.onOutgoingCallHangup(outgoing);
    }

    public void onOutgoingCallInvalid(Outgoing outgoing){
        Log.d("Phone", "onOutgoingCallInvalid");
        endPointListener1.onOutgoingCallInvalid(outgoing);
    }

    public void onIncomingDigitNotification(String digits) {
        Log.d("Phone", "onIncomingDigitNotification");
        endPointListener1.onIncomingDigitNotification(digits);
    }
}
