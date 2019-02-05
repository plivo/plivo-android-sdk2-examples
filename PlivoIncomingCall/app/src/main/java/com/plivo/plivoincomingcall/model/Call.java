package com.plivo.plivoincomingcall.model;

import android.text.TextUtils;

import com.plivo.plivoincomingcall.layer.plivo.PlivoCall;

public class Call {
    private String id;
    private String from;
    private String to;
    private Object data;
    private long createdAt;

    private PlivoCall.CALL_STATE state;
    private PlivoCall.CALL_TYPE type;

    private Call(String id, String from, String to, PlivoCall.CALL_STATE state, PlivoCall.CALL_TYPE type, Object data, long createdAt) {

        this.id = id;
        this.from = from;
        this.to = to;
        this.state = state;
        this.type = type;
        this.data = data;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public PlivoCall.CALL_STATE getState() {
        return state;
    }

    public void setState(PlivoCall.CALL_STATE state) {
        this.state = state;
    }

    public PlivoCall.CALL_TYPE getType() {
        return type;
    }

    public boolean isIncoming() {
        return type == PlivoCall.CALL_TYPE.INCOMING;
    }

    public boolean isOutgoing() {
        return type == PlivoCall.CALL_TYPE.OUTGOING;
    }

    public Object getData() {
        return data;
    }

    public boolean isRinging() { return state == PlivoCall.CALL_STATE.RINGING; }
    public boolean isHangedUp() { return state == PlivoCall.CALL_STATE.HANGUP; }
    public boolean isAnswered() { return state == PlivoCall.CALL_STATE.ANSWERED; }
    public boolean isIdle() { return state == PlivoCall.CALL_STATE.IDLE; }
    public boolean isInvalid() { return state == PlivoCall.CALL_STATE.INVALID; }
    public boolean isRejected() { return state == PlivoCall.CALL_STATE.REJECTED; }

    public long getCreatedAt() {
        return createdAt;
    }

    public static class Builder {
        private String id;
        private String fromContact, fromSip;
        private String toContact, toSip;
        private long createdAt;

        private PlivoCall.CALL_STATE state;
        private PlivoCall.CALL_TYPE type;

        private Object data;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setState(PlivoCall.CALL_STATE state) {
            this.state = state;
            return this;
        }

        public Builder setType(PlivoCall.CALL_TYPE type) {
            this.type = type;
            return this;
        }

        // "android2181024115535" <sip:+1000001@52.220.63.157>
        public Builder setFromContact(String fromContact) {
            this.fromContact = fromContact;
            return this;
        }

        // sip:+1000001
        public Builder setFromSip(String fromSip) {
            this.fromSip = fromSip;
            return this;
        }

        // <sip:android1181024115518@202.62.77.146:1904;transport=TLS;ob>
        public Builder setToContact(String toContact) {
            this.toContact = toContact;
            return this;
        }

        // sip:android1181024115518
        public Builder setToSip(String toSip) {
            this.toSip = toSip;
            return this;
        }

        // incoming/outgoing obj
        public Builder setData(Object data) {
            this.data = data;
            setCreatedAt(System.currentTimeMillis());
            return this;
        }

        private void setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
        }

        private String from() {
            String from = TextUtils.isEmpty(this.fromContact)?
                    TextUtils.isEmpty(this.fromSip)? "" : this.fromSip:
                    this.fromContact;
            return from.contains("\"") ?
                    from.substring(from.indexOf("\"")+1, from.lastIndexOf("\"")):
                    from;

        }

        private String to() {
            return TextUtils.isEmpty(this.toSip) ? "" :
                    this.toSip.substring(this.toSip.indexOf(":")+1);
        }

        public Call build() {
            return new Call(this.id,
                    from(),
                    to(),
                    state,
                    type,
                    data,
                    createdAt);
        }

    }
}
