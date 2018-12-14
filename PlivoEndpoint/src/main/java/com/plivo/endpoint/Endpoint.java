package com.plivo.endpoint;

import com.plivo.endpoint.backend.plivo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


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
	 * isInitialized flag.
	 */
	private boolean isInitialized;

	/**
	 * Current isActive outgoing call.
	 */
	private Outgoing curOutgoing;

	/**
	 * Registration flag.
	 */
	private boolean isRegistered;

	/**
	 * Registration Timeout in seconds - value to be specified between 120 & 86400 only
	 */
	private int regTimeout = 600;

	static {
		if (BuildConfig.JUNIT_EN) {
			try {
				System.loadLibrary("pjplivo");
				Log.log("libpjplivo loaded");
			} catch (UnsatisfiedLinkError ule) {
				Log.log("errload loading libpjplivo:" + ule.toString());
			}
		}
	}

	public Endpoint(boolean debug, EventListener eventListener) {
		Log.enable(debug);
		this.eventListener = eventListener;
		isInitialized = initLib(this.eventListener);
	}

	/**
	 * Create Plivo endpoint instance
	 * @param debug true if we want to set debug flag.
	 * @param eventListener event listener object.
	 * @return
	 */
	public static Endpoint newInstance(boolean debug, EventListener eventListener) {
		Endpoint endpoint = new Endpoint(debug, eventListener);
		Log.log("newInstance " + debug + "eventListener: " + eventListener);
		return endpoint.isInitialized ? endpoint : null;
	}

	/**
	 * Login to plivo cloud
	 * @param username Username of the endpoint
	 * @param password Password of the endpoint
	 * @return
	 */
	public boolean login(String username, String password) {
		if (plivo.Login(username, password, this.regTimeout) != 0) {
			logDebug("Login attempt failed. Check your username and password");
			return false;
		} else {
			logDebug("Login attempt success");
			return true;
		}
	}

	/**
	 * Logout
	 * @return
	 */
	public boolean logout() {
		if(this.isRegistered == true) {
			if (plivo.Logout() != 0) {
				logDebug("Logout failed");
				return false;
			}
		}
		logDebug("Logout success");
		return true;
	}

	/**
	 * Create outgoing call instance.
	 * @return
	 */
	public Outgoing createOutgoingCall () throws EndpointNotRegisteredException {
		logDebug("createOutgoingCall");
		if (!this.isRegistered) {
			logDebug("Endpoint not registered");
			throw new EndpointNotRegisteredException();
		} else {
			Outgoing out = new Outgoing(this);
			this.curOutgoing = out;
			return out;
		}
	}

	/**
	 * Check if a digit is valid dtmf digit
	 * @param digit Digit to be checked.
	 * @return
	 */
	public boolean checkDtmfDigit(String digit) {
		return Utils.VALID_DTMF.contains(digit);
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
		if ((regTimeout >= 120) && (regTimeout <= 86400)) {
			if (regTimeout != this.regTimeout) {
				this.regTimeout = regTimeout;
				if (this.isRegistered) {
					plivo.setRegTimeout(regTimeout);
				}
			}
		} else {
			logDebug("Allowed values of regTimeout are between 120 and 86400 seconds only");
		}
	}

	private void logDebug(String str) {
		Log.log("[endpoint]" + str);
	}

	private boolean initLib(EventListener eventListener) {
		loadJNI();

		if (backendListener == null) {
			backendListener = new BackendListener(Global.DEBUG, this, eventListener);
		}
		plivo.setCallbackObject(backendListener);

		logDebug("Starting module..");

		int rc = plivo.plivoStart();

		if (rc != 0) {
			logDebug("plivolib failed. rc = " + rc);
			logDebug("Failed to initialize Plivo Endpoint object");
			return false;
		} else {
			logDebug("plivolib started.....");
		}
		return true;
	}

	private void loadJNI() {
		if (BuildConfig.JUNIT_EN) return;

		try {
			System.loadLibrary("pjplivo");
			logDebug("libpjplivo loaded");
		} catch (UnsatisfiedLinkError ule) {
			logDebug("errload loading libpjplivo:" + ule.toString());
		}
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
		String push_str = Utils.mapToString(push_headers);

		if(push_str.length() > 0) {
			plivo.relayVoipPushNotification(push_str);
		}else{
			System.out.println("Invalid Notification");
		}
	}

	public class EndpointNotRegisteredException extends Exception {
		public EndpointNotRegisteredException() {
			super("Endpoint not registered or expired");
		}
	}
}

