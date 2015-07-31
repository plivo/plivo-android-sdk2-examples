package com.plivo.endpoint;

import com.plivo.endpoint.backend.plivo;

import java.util.*;

public class Outgoing {
	private String toContact;
	private String callId;
	protected int pjsuaCallId;
	private Endpoint endpoint;
	private boolean active;
	/**
	 * muted flag. True if this outgoing call is muted.
	 */

	private boolean isMuted;

	public Outgoing(Endpoint endpoint) {
		this.endpoint = endpoint;
		this.isMuted = false;
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

	/* Call method with headers */
	/* the map during initialization should be ConcurrentHashMap */
	public boolean callH(String dest, Map<String, String> headers) {
		String sipUri = "sip:" + dest + "@phone.plivo.com";
		this.toContact = sipUri;
		active = true;
		//Outgoing.checkSpecialCharacters(headers);
		String headers_str = Outgoing.mapToString(headers);


		if (plivo.CallH(sipUri, headers_str) != 0) {
			System.out.println("Call attempt failed. Check you destination address");
			active = false;
			return false;
		}
		return true;
	}

	public static String mapToString(Map<String, String> map) {  
       StringBuilder stringBuilder = new StringBuilder();  
      
       for (String key : map.keySet()) {  
        if (stringBuilder.length() > 0) {  
         stringBuilder.append(",");  
        }  
        String value = map.get(key);    
         stringBuilder.append(key);  
         stringBuilder.append(":");  
         stringBuilder.append(value);   
       }
       return stringBuilder.toString();  
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



	/**
	 * Hang up a call
	 */
	public void hangup() {
		active = false;
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
	
	public boolean isActive() {
		return active;
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