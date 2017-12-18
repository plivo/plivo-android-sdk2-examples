package com.plivo.endpoint;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.*; 

import com.plivo.endpoint.backend.plivo;

public class Incoming {
	private String fromContact;
	private String toContact;
	private String callId;
	private String header;
	private int pjsuaCallId;
	private boolean isMuted;
	private boolean active;
	private final Set<String> validDtmfList = new HashSet<String>(Arrays.asList(
		     new String[] {"0","1","2","3", "4", "5", "6", "7", "8", "9", "#", "*"}
		));


	public Incoming(int pjsuaCallId, String callId, String fromContact, String toContact, String header) {
		this.callId = callId;
		this.fromContact = fromContact;
		this.toContact = toContact;
		this.pjsuaCallId = pjsuaCallId;
		this.header = header;
		this.isMuted = false;

	}

	/**
	 * Answer an incoming call
	 */
	public void answer() {

		if(!isActive()) {

			active = true;
			plivo.Answer(this.pjsuaCallId);
		}
	}

	/**
	 * Hangup an incoming call
	 */
	public void hangup() {

		if(isActive()) {

			active = false;
			plivo.Hangup(this.pjsuaCallId);
		}
	}

	/**
	 *
	 * @param digit DTMF digit to be sent
	 * @return true if valid digit, false otherwise.
	 */
	public boolean sendDigits(String digit) {
		if(isActive()) {
			if (this.validDtmfList.contains(digit)) {
				plivo.SendDTMF(this.pjsuaCallId, digit);
				return true;
			}
			return false;
		}else{
			return false;
		}
	}

	public boolean isActive() {
		return active;
	}

	public void reject() {

		if(!isActive()) {

			plivo.Reject(this.pjsuaCallId);

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
        return map;
    }

	public String getFromContact() {
		return fromContact;
	}
	public String getToContact() {
		return toContact;
	}
	public String getFromSip() {
		String string = getFromContact();
		String[] parts = string.split("@");
		String part1 = parts[0];
		String[] parts_1 = part1.split("<");
		String caller = parts_1[0];
		String sipname = parts_1[1];
		return sipname;
	}
	public String getToSip() {
		String string = getToContact();
		String[] parts = string.split("@");
		String part1 = parts[0];
		String[] parts_1 = part1.split("<");
		String sipid = parts_1[1];
		return sipid;
	}
	public String getCallId() {
		return callId;
	}
}
