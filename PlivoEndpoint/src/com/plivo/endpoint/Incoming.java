package com.plivo.endpoint;

import com.plivo.endpoint.backend.plivo;

public class Incoming {
	private String fromContact;
	private String toContact;
	private String callId;
	private int pjsuaCallId;
	
	public Incoming(int pjsuaCallId, String callId, String fromContact, String toContact) {
		this.callId = callId;
		this.fromContact = fromContact;
		this.toContact = toContact;
		this.pjsuaCallId = pjsuaCallId;
	}
	
	public void answer() {
		plivo.Answer(this.pjsuaCallId);
	}
	
	public void hangup() {
		plivo.Hangup(this.pjsuaCallId);
	}

	public void reject() {
		plivo.Reject(this.pjsuaCallId);
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
