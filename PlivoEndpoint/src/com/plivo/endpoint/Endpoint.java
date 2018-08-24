package com.plivo.endpoint;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.*;

import com.plivo.endpoint.backend.plivo;


public class Endpoint {
	/**
	 * Listener for PJSIP event.
	 */
	private BackendListener backendListener;

	/**
	 * Event listener that need to be implemented by user.
	 */
	private EventListener eventListener;

	/**
	 * initialized flag.
	 */
	private boolean initialized = false;

	/**
	 * debug flag.
	 */
	private boolean debug;

	/**
	 * Current active outgoing call.
	 */
	private Outgoing curOutgoing;

	/**
	 * Registration flag.
	 */
	private boolean isRegistered;

	/**
	 * Registration Timeout in seconds - value to be specified between 60 & 86400 only
	 */
	private int regTimeout = 600;

	private final Set<String> validDtmfList = new HashSet<String>(Arrays.asList(
			new String[] {"0","1","2","3", "4", "5", "6", "7", "8", "9", "#", "*"}
	));

	public Endpoint(boolean debug, EventListener eventListener) {
		this.eventListener = eventListener;
		Global.DEBUG = debug;

		if (initLib(this.eventListener) == true) {
			initialized = true;
		} else {
			logDebug("Failed to initialize Plivo Endpoint object");
		}
//      this.debug = debug;
		this.isRegistered = false;
	}

	/**
	 * Create Plivo endpoint instance
	 * @param debug true if we want to set debug flag.
	 * @param eventListener event listener object.
	 * @return
	 */
	public static Endpoint newInstance(boolean debug, EventListener eventListener) {
		Endpoint endpoint = new Endpoint(debug, eventListener);
		if (endpoint.initialized == false) {
			return null;
		}
		return endpoint;
	}

	/**
	 * Login to plivo cloud
	 * @param username Username of the endpoint
	 * @param password Password of the endpoint
	 * @return
	 */
	public boolean login(String username, String password) {
		if ((this.regTimeout >= 60) && (this.regTimeout <= 86400)) {
			if (plivo.Login(username, password, this.regTimeout) != 0) {
				logDebug("Login attempt failed. Check your username and password");
			return false;
			} else {
				logDebug("Login attempt success");
				return true;
			}
		} else {
			logDebug("Login attempt failed. Allowed values of regTimeout are between 60 and 86400 seconds only");
			return false;
		}
		
	}

	/**
	 * Logout
	 * @return
	 */
	public boolean logout() {
		if(this.isRegistered == true) {
			if (plivo.Logout() != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Create outgoing call instance.
	 * @return
	 */
	public Outgoing createOutgoingCall () {
		if (!this.isRegistered) {
			return null;
		}
		Outgoing out = new Outgoing(this);
		this.curOutgoing = out;
		return out;
	}

	/**
	 * Check if a digit is valid dtmf digit
	 * @param digit Digit to be checked.
	 * @return
	 */
	public boolean checkDtmfDigit(String digit) {

		return this.validDtmfList.contains(digit);
	}

	protected Outgoing getOutgoing() {

		return this.curOutgoing;
	}

	protected void setRegistered(boolean status) {

		this.isRegistered = status;
	}

	public boolean getRegistered(){

		return this.isRegistered;
	}

	public void setRegTimeout(int regTimeout) {
		if ((regTimeout >= 60) && (regTimeout <= 86400)) {
			if (regTimeout != this.regTimeout) {
				this.regTimeout = regTimeout;
				if (this.isRegistered) {
					plivo.setRegTimeout(regTimeout);
				}
			}
		} else {
			logDebug("Allowed values of regTimeout are between 60 and 86400 seconds only");
		}
	}

	private void logDebug(String str) {
		if (Global.DEBUG) {
			System.out.println("[endpoint]" + str);
		}
	}

	private boolean initLib(EventListener eventListener) {
		try {
			System.loadLibrary("pjplivo");
			System.out.println("libpjplivo loaded");
		} catch (UnsatisfiedLinkError ule) {
			System.out.println("errload loading libpjplivo:" + ule.toString());
		}

		if (backendListener == null) {
			backendListener = new BackendListener(Global.DEBUG, this, eventListener);
		}
		plivo.setCallbackObject(backendListener);

		System.out.println("Starting module..");

		int rc = plivo.plivoStart();

		if (rc != 0) {
			System.out.println("plivolib failed. rc = " + rc);
			return false;
		} else {
			System.out.println("plivolib started.....");
		}
		return true;
	}

	// Creating application in keep alive
	public void keepAlive(){

		plivo.keepAlive();
	}
	//reset the endpoint when the network has change
	public void resetEndpoint(){

		plivo.resetEndpoint();
	}

	//Register Deivce token with Plivo.
	//You can get the registration token (means that your device has successfully registered) from FCM or GCM
	public void registerToken(String deviceToken)
	{
		if(deviceToken.length() > 0) {
			plivo.registerToken(deviceToken);
		}else {
			System.out.println("Invalid Token");
		}
	}

	//Push_headers is the Map object forwarded by the GCM or FCM push notification service.
	public void relayVoipPushNotification(Map<String, String> push_headers)
	{
		String push_str = Global.mapToString(push_headers);

		if(push_str.length() > 0) {
			plivo.relayVoipPushNotification(push_str);
		}else{
			System.out.println("Invalid Notification");
		}
	}
}
