package com.plivo.endpoint;

import android.text.TextUtils;

import com.plivo.endpoint.backend.plivo;

import java.util.*;

public class Outgoing extends IO {
	private Endpoint endpoint;

	public Outgoing(Endpoint endpoint) {
		this.endpoint = endpoint;
		isOnMute = false;
		isActive =false;
	}

	/**
	 * Call an endpoint.
	 * @param dest
	 * @return
	 */
	public boolean call(String dest) {
		Log.D("call " + dest);
		if (TextUtils.isEmpty(dest)) {
			Log.E("Call Cannot be Placed. Entered SIP endpoint is empty.");
			return false;
		}

		String sipUri = "sip:" + dest + "@" + Global.DOMAIN;
		setToContact(sipUri);
		if (plivo.Call(sipUri) != 0) {
			Log.E("Call attempt failed. Check you destination address");
			isActive = false;
			return false;
		}
		Log.D("Call Placed");
		isActive = true;
		return true;
	}

	/* Call method with headers */
	/* the map during initialization should be ConcurrentHashMap */
	public boolean callH(String dest, Map<String, String> headers) {
		Log.D("callH " + dest + "headers:" + headers);
		if (TextUtils.isEmpty(dest)) {
			Log.E("Call Cannot be Placed. Entered SIP endpoint is empty.");
			return false;
		}

		String sipUri = "sip:" + dest + "@" + Global.DOMAIN;
		setToContact(sipUri);
		isActive = true;
		String headers_str = Utils.mapToString(headers);
		if (TextUtils.isEmpty(headers_str)) {
			Log.E("Call Cannot be Placed. Invalid headers.");
			return false;
		}

		if (plivo.CallH(sipUri, headers_str) != 0) {
			Log.E("Call attempt failed. Check you destination address");
			isActive = false;
			return false;
		}
		Log.D("Call Placed");
		return true;
	}

	// retaining this to not break the backward compatibility
	public static void checkSpecialCharacters(Map<String, String> map) {
		Utils.checkSpecialCharacters(map);
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
