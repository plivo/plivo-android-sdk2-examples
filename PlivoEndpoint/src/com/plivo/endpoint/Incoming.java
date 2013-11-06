package com.plivo.endpoint;

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
		System.out.println("callId = " + callId);
		System.out.println("fromContact = " + fromContact);
	}
	
	public void answer() {
		System.out.println("TBD:anwering call id="+this.pjsuaCallId);
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
