package com.plivo.endpoint;

import com.plivo.endpoint.backend.plivo;

public class Outgoing {
	private String toContact;
	private String callId;
	private int pjsuaCallId;
	public Outgoing() {
		
	}
	
	public boolean call(String dest) {
		String sipUri = "sip:" + dest + "@phone.plivo.com";
		if (plivo.Call(sipUri) != 0) {
			System.out.println("Call attempt failed. Check you destination address");
			return false;
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
}
