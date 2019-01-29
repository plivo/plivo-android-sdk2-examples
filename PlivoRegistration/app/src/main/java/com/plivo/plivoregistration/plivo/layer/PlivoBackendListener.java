package com.plivo.plivoregistration.plivo.layer;

public class PlivoBackendListener {
    public interface LoginListener {
        void onLogin(boolean success);
    }

    public interface LogoutListener {
        void logout();
    }
}
