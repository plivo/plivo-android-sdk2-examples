package com.plivo.endpoint;

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
		Log.log("call " + dest);
		if(dest.length() > 0) {

			String sipUri = "sip:" + dest + "@" + Global.DOMAIN;
			setToContact(sipUri);
			isActive = true;

			if (plivo.Call(sipUri) != 0) {
				Log.log("Call attempt failed. Check you destination address");
				isActive = false;
				return false;
			}
			return true;
		}
		return false;

	}

	/* Call method with headers */
	/* the map during initialization should be ConcurrentHashMap */
	public boolean callH(String dest, Map<String, String> headers) {
		Log.log("callH " + dest + "headers:" + headers);
		if(dest.length() > 0) {
			String sipUri = "sip:" + dest + "@" + Global.DOMAIN;
			setToContact(sipUri);
			isActive = true;
			//Outgoing.checkSpecialCharacters(headers);
			String headers_str = Global.mapToString(headers);

			if (plivo.CallH(sipUri, headers_str) != 0) {
				Log.log("Call attempt failed. Check you destination address");
				isActive = false;
				return false;
			}
			return true;
		}
		return false;
	}


	public static void checkSpecialCharacters(Map<String, String> map) {
		int header_length = map.size();
		if (header_length > 0) {
			String words = "abcdefghijklmnopqrstvwxyzABCDEFGHIJKLMNOPQRSTUVWXTZ0123456789-";
			for (String key : map.keySet()) {
				String value = map.get(key);
				for(int i=0; i< key.length(); i++){
					if (!words.contains(String.valueOf(key.charAt(i)) )) {
						System.out.println(key + ":" + value + " contains characters that aren't allowed");
						map.remove(key);
						key = null;
						break;
					}
				}
				if (key == null)
					continue;
				for(int i=0; i< value.length(); i++){
					if (!words.contains(String.valueOf(value.charAt(i)) )) {
						System.out.println(key + ":" + value + " contains characters that aren't allowed");
						map.remove(key);
						break;
					}
				}

				if ((!key.startsWith("X-PH-")  && !key.startsWith("X-Ph-")) || (key.length() > 24) ||
						(value.length() > 48)) {
					System.out.println("Skipping " + key + ":" + value);
					map.remove(key);
				}
			}
		}

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
