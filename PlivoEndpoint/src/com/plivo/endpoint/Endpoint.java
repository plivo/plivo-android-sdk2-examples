package com.plivo.endpoint;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

	private final Set<String> validDtmfList = new HashSet<String>(Arrays.asList(
		     new String[] {"0","1","2","3", "4", "5", "6", "7", "8", "9", "#", "*"}
		));

	private Endpoint(boolean debug, EventListener eventListener) {
		this.eventListener = eventListener;
		if (initLib(this.eventListener) == true) {
			initialized = true;
		} else {
			logDebug("Failed to initialize Plivo Endpoint object");
		}
		this.debug = debug;
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
		if (plivo.Login(username, password) != 0) {
			logDebug("Login attempt failed. Check your username and password");
			return false;
		}
		System.out.println("Login...");
		return true;
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

	private void logDebug(String... strs) {
		if (this.debug) {
			System.out.println(strs);
		}
	}
	private boolean initLib(EventListener eventListener) {
		try {
			System.loadLibrary("pjplivo");
			System.out.println("libpjplivo loaded");
		} catch (UnsatisfiedLinkError ule) {
			System.out.println("errload loading libpjplivo:" + ule.toString());
		}

		// Wait for GDB to init
		/*if ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				//LOG.ERROR(ui_handler, "InterruptedException: " + e.getMessage());
				System.out.println("InterruptedException: " + e.getMessage());
			}
		}*/

		if (backendListener == null) {
			backendListener = new BackendListener(this.debug, this, eventListener);
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
}
