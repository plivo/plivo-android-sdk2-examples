package com.plivo.plivoincomingcall.model.internal;

/**
 * Used for observing telephony or cellular call states
 */
public class TelephonyCall {
    private int state;
    private String phone_number;

    public TelephonyCall(int state, String phone_number) {
        this.state = state;
        this.phone_number = phone_number;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }
}
