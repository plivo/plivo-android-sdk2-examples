package com.plivo.endpoint;

import com.plivo.endpoint.backend.plivo;

import java.util.*;

public class Incoming extends IO {
	private String fromContact;
	private String callId;
	private String header;

	public Incoming(int pjsuaCallId, String callId, String fromContact, String toContact, String header) {
		this.callId = callId;
		this.fromContact = fromContact;
		this.toContact = toContact;
		this.pjsuaCallId = pjsuaCallId;
		this.header = header;
		this.isOnMute = false;
		this.isActive=false;
	}

	/**
	 * Answer an incoming call
	 */
	public void answer() {
		if (isActive()) {
			Log.E("cannot answer: call is already answered. Try hangup()");
		}
		Log.D("answer" );
		isActive = true;
		plivo.Answer(this.pjsuaCallId);
	}

	public void reject() {
		if (isActive()) {
			Log.E("cannot reject: call is already active. Try hangup()");
			return;
		}

		Log.D("reject");
		plivo.Reject(this.pjsuaCallId);
	}

	public String getHeader() {
		return header;
	}

	public Map<String, String> getHeaderDict(){
        Map<String, String> map = Utils.stringToMap(header);
		Log.D("getHeaderDict: " + map);
        return map;
    }

    // From format: <sip:android1181024115518@phone.plivo.com>;tag=1r7387ubi7
	public String getFromContact() {
		return fromContact;
	}

	public String getFromSip() {
		String from = getFromContact();
		if (!from.contains("<") || !from.contains("@")) return null;

		String sipName = from.substring(from.indexOf("<") + 1, from.indexOf("@"));
		Log.D("getFromSip: " + sipName);
		return sipName;
	}

	public String getToSip() {
		String to = getToContact();
		if (!to.contains("<") || !to.contains("@")) return null;

		String sipId = to.substring(to.indexOf("<") + 1, to.indexOf("@"));
		Log.D("getToSip: " + sipId);
		return sipId;
	}

	public String getCallId() {
		return callId;
	}
}
