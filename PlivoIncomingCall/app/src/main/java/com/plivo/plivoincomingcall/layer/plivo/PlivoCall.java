package com.plivo.plivoincomingcall.layer.plivo;

import com.plivo.plivoincomingcall.model.Call;

public class PlivoCall {

    public enum CALL_TYPE {
        INCOMING,
        OUTGOING
    }

    public enum CALL_STATE {
        IDLE,
        RINGING, // ringing after call is outgoing/incoming
        ANSWERED, // outgoing/incoming call is answered
        HANGUP,
        REJECTED,
        INVALID // made a out call to invalid phone number
    }

    private Call call;

    public Call getCurrentCall() {
        return call;
    }

    public void setCurrentCall(Call call) {
        if (call == null) return;

        this.call = call;
    }
}
