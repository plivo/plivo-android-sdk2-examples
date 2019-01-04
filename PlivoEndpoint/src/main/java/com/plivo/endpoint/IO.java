package com.plivo.endpoint;

import android.text.TextUtils;

import com.plivo.endpoint.backend.plivo;

class IO {
    private static final int MAX_DTMF_DIGITS = 24;
    protected String toContact;
    protected String callId;
    protected int pjsuaCallId;

    protected boolean isActive;
    protected boolean isOnMute;
    protected boolean isOnHold;

    public boolean isActive() {
        return isActive;
    }

    public boolean checkDtmfDigit(String digit) {
        return Utils.VALID_DTMF.contains(digit);
    }

    public void setToContact(String toContact) {
        this.toContact = toContact;
    }

    // To format: <sip:android1181024115518@phone.plivo.com>
    public String getToContact() {
        return toContact;
    }

    /**
     * Send DTMF digit
     * @param digit
     */
    public boolean sendDigits(String digit) {
        if (TextUtils.isEmpty(digit) || digit.length() > MAX_DTMF_DIGITS) {
            Log.E("digit.length() is empty or greater than MAX_LIMIT 24 "+ digit.length());
            return false;
        }

        if (!checkDtmfDigit(digit)) {
            Log.E("Invalid DTMF digit");
            return false;
        }

        Log.D("send DTMF digit - "+ this.pjsuaCallId +" "+ digit);
        plivo.SendDTMF(this.pjsuaCallId, digit);
        return true;
    }

    /**
     * Hang up a call
     */
    public void hangup() {
        if (!isActive()) {
            Log.E("Call is not active! Hangup can be applied only on active call.");
            return;
        }

        Log.D("hangup");
        isActive = false;
        plivo.Hangup(this.pjsuaCallId);
    }

    public boolean mute() {
        if (isOnMute) {
            Log.D("Already on mute");
            return true;
        }

        Log.D("mute");
        if (plivo.Mute(pjsuaCallId) != 0) {
            Log.E("mute failed");
            return false;
        }

        isOnMute = true;
        Log.D("mute success");
        return true;
    }

    public boolean unmute() {
        if (!isOnMute) {
            Log.D("Already umute");
            return true;
        }

        Log.D("unmute");
        if (plivo.UnMute(pjsuaCallId) != 0) {
            Log.E("unmute failed");
            return false;
        }

        isOnMute = false;
        Log.D("unmute success");
        return true;
    }

    public boolean hold() {
        if (isOnHold) {
            Log.D("Already hold");
            return true;
        }

        Log.D("hold pjsuaCallId " + pjsuaCallId);
        if (plivo.Hold(pjsuaCallId) != 0) {
            Log.E("hold failed");
            return false;
        }

        isOnHold = true;
        Log.D("hold success");
        return true;
    }

    public boolean unhold() {
        if (!isOnHold) {
            Log.D("already unhold");
            return true;
        }

        Log.D("unhold pjsuaCallId " + pjsuaCallId);
        if (plivo.UnHold(pjsuaCallId) != 0) {
            Log.D("unhold failed");
            return false;
        }

        isOnHold = false;
        Log.D("unhold success");
        return true;
    }
}


