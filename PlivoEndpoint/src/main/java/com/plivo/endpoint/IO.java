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
                Log.log("send DTMF digit - "+ this.pjsuaCallId +" "+ digit);
                plivo.SendDTMF(this.pjsuaCallId, digit);
                return true;
            } else {
                Log.log("check DTMF digit - false");
                return false;
            }
        } else {
            //System.out.println("isActive() - false");
            Log.log("digit.length() is greater than 24 "+ digit.length());
            return false;
        }
    }

    /**
     * Hang up a call
     */
    public void hangup() {
        if (isActive()) {
            isActive = false;
            plivo.Hangup(this.pjsuaCallId);
        }
    }

    public boolean mute() {
        if (!this.isOnMute) {
            if (plivo.Mute(this.pjsuaCallId) == 0) {
                this.isOnMute = true;
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean unmute() {
        if (this.isOnMute) {
            if (plivo.UnMute(this.pjsuaCallId) == 0) {
                this.isOnMute = false;
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean hold() {
        if (!this.isOnHold) {
            if (plivo.Hold(this.pjsuaCallId) == 0) {
                this.isOnHold = true;
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean unhold() {
        if (!this.isOnHold) {
            if (plivo.UnHold(this.pjsuaCallId) == 0) {
                this.isOnHold = false;
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

}


