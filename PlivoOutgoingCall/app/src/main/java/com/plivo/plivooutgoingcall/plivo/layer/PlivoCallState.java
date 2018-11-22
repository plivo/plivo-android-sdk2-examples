package com.plivo.plivooutgoingcall.plivo.layer;

public class PlivoCallState {
    public enum OUT_CALL_STATE {
        IDLE,
        RINGING, // ringing after call is made
        ANSWERED, // outgoing call is answered
        HANGUP, // outgoing call disconnected after answering
        REJECTED, // rejected by the outgoing call receiver endpoint
        INVALID // made a out call to invalid phone number
    }
}
