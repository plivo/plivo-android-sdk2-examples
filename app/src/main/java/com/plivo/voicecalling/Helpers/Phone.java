package com.plivo.voicecalling.Helpers;

import android.util.Log;

import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.EventListener;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;

import java.util.Map;

/**
 * Created by Siva on 05/06/17.
 */

public class Phone implements EventListener
{
    private static final Phone ourInstance = new Phone();

    public Endpoint endpoint;
    public Incoming incoming;


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

    public boolean logout() {

        return endpoint.logout();
    }

    public void keepAlive() {

        endpoint.keepAlive();
    }

    public Outgoing createOutgoingCall() {

        return endpoint.createOutgoingCall();
    }

    public void registerToken(String token) {

        endpoint.registerToken(token);
    }


    public void relayVOIPNotification(Map<String, String> notification) {

        endpoint.relayVoipPushNotification(notification);
    }


    public void onLogin() {

        Log.d("Phone - Singleton", "Logging in");
        endPointListener1.onLogin();

    }

    public void onLogout() {

        endPointListener1.onLogout();

    }

    public void onLoginFailed() {

        Log.d("PlivoInbound", "Login failed");
        endPointListener1.onLoginFailed();

    }

    public void onIncomingCall(Incoming incoming) {

        endPointListener1.onIncomingCall(incoming);

    }

    public void onIncomingCallHangup(Incoming incoming) {
        endPointListener1.onIncomingCallHangup(incoming);

    }

    public void onIncomingCallRejected(Incoming incoming) {
        endPointListener1.onIncomingCallRejected(incoming);

    }

    public void onOutgoingCall(Outgoing outgoing) {
        endPointListener1.onOutgoingCall(outgoing);

    }

    public void onOutgoingCallAnswered(Outgoing outgoing) {
        endPointListener1.onOutgoingCallAnswered(outgoing);

    }

    public void onOutgoingCallRejected(Outgoing outgoing) {
        endPointListener1.onOutgoingCallRejected(outgoing);

    }

    public void onOutgoingCallHangup(Outgoing outgoing) {
        endPointListener1.onOutgoingCallHangup(outgoing);

    }

    public void onOutgoingCallInvalid(Outgoing outgoing){
        endPointListener1.onOutgoingCallInvalid(outgoing);

    }

    public void onIncomingDigitNotification(String digits) {
        endPointListener1.onIncomingDigitNotification(digits);

    }
}
