package com.plivo.endpoint;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.plivo.endpoint.backend.plivo;

public class Incoming {
	private String fromContact;
	private String toContact;
	private String callId;
	private int pjsuaCallId;
	private boolean isMuted;
	private boolean active;
	private final Set<String> validDtmfList = new HashSet<String>(Arrays.asList(
		     new String[] {"0","1","2","3", "4", "5", "6", "7", "8", "9", "#", "*"}
		));


	public Incoming(int pjsuaCallId, String callId, String fromContact, String toContact) {
		this.callId = callId;
		this.fromContact = fromContact;
		this.toContact = toContact;
		this.pjsuaCallId = pjsuaCallId;

		this.isMuted = false;

	}

	/**
	 * Answer an incoming call
	 */
	public void answer() {
		active = true;
		plivo.Answer(this.pjsuaCallId);
	}

	/**
	 * Hangup an incoming call
	 */
	public void hangup() {
		active = false;
		plivo.Hangup(this.pjsuaCallId);
	}

	/**
	 *
	 * @param digit DTMF digit to be sent
	 * @return true if valid digit, false otherwise.
	 */
	public boolean sendDigits(String digit) {
		if (this.validDtmfList.contains(digit)) {
			plivo.SendDTMF(this.pjsuaCallId, digit);
			return true;
		}
		return false;
	}

	public boolean isActive() {
		return active;
	}

	public void reject() {
		plivo.Reject(this.pjsuaCallId);
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


	public String getFromContact() {
		return fromContact;
	}
	public String getToContact() {
		return toContact;
	}
	public String getCallId() {
		return callId;
	}
}
