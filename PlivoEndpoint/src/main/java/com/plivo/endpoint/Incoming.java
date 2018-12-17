package com.plivo.endpoint;

import com.plivo.endpoint.backend.plivo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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
		if(!isActive()) {
			Log.D("answer" );
			isActive = true;
			plivo.Answer(this.pjsuaCallId);
		} else {
			Log.E("cannot answer: call is already answered. Try hangup()");
		}
	}

	public void reject() {
		if(!isActive()) {
			Log.D("reject");
			plivo.Reject(this.pjsuaCallId);
		} else {
			Log.E("cannot reject: call is already active. Try hangup()");
		}
	}

	public String getHeader() {
		return header;
	}

	public Map<String, String> getHeaderDict(){
        String[] keyValuePairs = header.split(",");
        Map<String,String> map = new HashMap<String, String>();
        String string_1 = header.replace("\n", "");
        for(String pair : keyValuePairs){
            String[] entry = pair.split(":");
            map.put(entry[0].trim(), entry[1].trim());
        }
        Log.D("getHeaderDict: " + map);
        return map;
    }

	public String getFromContact() {
		return fromContact;
	}

	public String getFromSip() {
		String string = getFromContact();
		String[] parts = string.split("@");
		String part1 = parts[0];
		String[] parts_1 = part1.split("<");
		String caller = parts_1[0];
		String sipname = parts_1[1];
		Log.D("getFromSip: " + sipname);
		return sipname;
	}

	public String getToSip() {
		String string = getToContact();
		String[] parts = string.split("@");
		String part1 = parts[0];
		String[] parts_1 = part1.split("<");
		String sipid = parts_1[1];
		Log.D("getToSip: " + sipid);
		return sipid;
	}
	public String getCallId() {
		return callId;
	}
}
