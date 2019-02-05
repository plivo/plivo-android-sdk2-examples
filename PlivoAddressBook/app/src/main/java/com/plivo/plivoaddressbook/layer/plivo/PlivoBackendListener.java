package com.plivo.plivoaddressbook.layer.plivo;

import com.plivo.plivoaddressbook.model.Call;

public class PlivoBackendListener {
    public interface LoginListener {
        void onLogin(boolean success);
    }

    public interface LogoutListener {
        void logout();
    }

    public interface CallStackListener {
        void onCallChanged(Call call);
    }

    public interface DTMFListener {
        void onDTMFReceived(String digit);
    }
}
