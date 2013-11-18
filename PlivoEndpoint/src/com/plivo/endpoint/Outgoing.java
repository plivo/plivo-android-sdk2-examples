package com.plivo.endpoint;

import com.plivo.endpoint.backend.plivo;

public class Outgoing {
	private String toContact;
	private String callId;
	protected int pjsuaCallId;
	private Endpoint endpoint;
	
	/**
	 * muted flag. True if this outgoing call is muted.
	 */
	private boolean isMuted;
	public Outgoing(Endpoint endpoint) {
		this.endpoint = endpoint;
		this.isMuted = true;
	}
	
	/**
	 * Call an endpoint.
	 * @param dest
	 * @return
	 */
	public boolean call(String dest) {
		String sipUri = "sip:" + dest + "@phone.plivo.com";
		this.toContact = sipUri;
		if (plivo.Call(sipUri) != 0) {
			System.out.println("Call attempt failed. Check you destination address");
			return false;
		}
		return true;
	}
	
	/**
	 * Hang up a call
	 */
	public void hangup() {
		plivo.Hangup(this.pjsuaCallId);
	}
	
	/**
	 * Send DTMF digit
	 * @param digit
	 */
	public boolean sendDigits(String digit) {
		if (this.endpoint.checkDtmfDigit(digit)) {
			plivo.SendDTMF(this.pjsuaCallId, digit);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean mute() {
		if (!this.isMuted) {
			if (plivo.Mute(this.pjsuaCallId) == 0) {
				this.isMuted = true;
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	
	public boolean unmute() {
		if (this.isMuted) {
			if (plivo.UnMute(this.pjsuaCallId) == 0) {
				this.isMuted = false;
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	
	public String getToContact() {
		return toContact;
	}
	public void setToContact(String toContact) {
		this.toContact = toContact;
	}
	public String getCallId() {
		return callId;
	}
	public void setCallId(String callId) {
		this.callId = callId;
	}
	
	public String toString() {
		String str = "[Plivo Outgoing Call]callId = " + this.callId + ". to = " + this.toContact;
		return str;
	}
}
