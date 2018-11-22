package com.plivo.plivooutgoingcall.plivo.layer;

public class PlivoBackendListener {
    public interface LoginListener {
        void onLogin(boolean success);
    }

    public interface LogoutListener {
        void logout();
    }

    public interface CallListener {
        void onCall(PlivoCallState.OUT_CALL_STATE state);
    }
}
