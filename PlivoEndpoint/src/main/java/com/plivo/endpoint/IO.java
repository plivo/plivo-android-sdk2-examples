package com.plivo.endpoint;

import com.plivo.endpoint.backend.plivo;

class IO {
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
    public String getToContact() {
        return toContact;
    }

    /**
     * Send DTMF digit
     * @param digit
     */
    public boolean sendDigits(String digit) {

        //if(isActive() && digit.length() <= 24) { // SUP 113
        if(digit.length() <= 24) {
            if (checkDtmfDigit(digit)) {
                Log.D("send DTMF digit - "+ this.pjsuaCallId +" "+ digit);
                plivo.SendDTMF(this.pjsuaCallId, digit);
                return true;
            } else {
                Log.E("Invalid DTMF digit");
                return false;
            }
        } else {
            //System.out.println("isActive() - false");
            Log.E("digit.length() is greater than 24 "+ digit.length());
            return false;
        }
    }

    /**
     * Hang up a call
     */
    public void hangup() {
        if (isActive()) {
            Log.D("hangup");
            isActive = false;
            plivo.Hangup(this.pjsuaCallId);
        } else {
            Log.E("Call is not active! Hangup can be applied on active call only.");
        }
    }

    public boolean mute() {
        if (!this.isOnMute) {
            Log.D("mute");
            if (plivo.Mute(this.pjsuaCallId) == 0) {
                this.isOnMute = true;
                Log.D("mute success");
                return true;
            } else {
                Log.E("mute failed");
                return false;
            }
        }
        Log.D("Already mute");
        return true;
    }

    public boolean unmute() {
        if (this.isOnMute) {
            Log.D("unmute");
            if (plivo.UnMute(this.pjsuaCallId) == 0) {
                this.isOnMute = false;
                Log.D("unmute success");
                return true;
            } else {
                Log.E("unmute failed");
                return false;
            }
        }
        Log.D("Already umute");
        return true;
    }

    public boolean hold() {
        if (!this.isOnHold) {
            Log.D("hold");
            if (plivo.Hold(this.pjsuaCallId) == 0) {
                this.isOnHold = true;
                Log.D("hold success");
                return true;
            } else {
                Log.E("hold failed");
                return false;
            }
        }
        Log.D("Already hold");
        return true;
    }

    public boolean unhold() {
        if (!this.isOnHold) {
            Log.D("unhold");
            if (plivo.UnHold(this.pjsuaCallId) == 0) {
                this.isOnHold = false;
                Log.D("unhold success");
                return true;
            } else {
                Log.D("unhold failed");
                return false;
            }
        }
        Log.D("already unhold");
        return true;
    }

}


