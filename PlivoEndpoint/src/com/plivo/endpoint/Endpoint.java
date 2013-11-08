package com.plivo.endpoint;

import com.plivo.endpoint.backend.plivo;


public class Endpoint {
	private BackendListener eventListener;
	private boolean initialized = false;
	private boolean debug;
	
	public BackendListener getEventListener() {
		return eventListener;
	}

	public void setEventListener(BackendListener eventListener) {
		this.eventListener = eventListener;
		plivo.setCallbackObject(eventListener);
	}
	
	private Endpoint(boolean debug) {
		this.debug = debug;
		if (initLib() == true) {
			initialized = true;
		} else {
			logDebug("Failed to initialize Plivo Endpoint object");
		}
	}
	
	public static Endpoint newInstance(boolean debug) {
		Endpoint endpoint = new Endpoint(debug);
		if (endpoint.initialized == false) {
			return null;
		}
		return endpoint;
	}
	
	public boolean login(String username, String password) {
		if (plivo.Login(username, password) != 0) {
			logDebug("Login attempt failed. Check your username and password");
			return false;
		}
		System.out.println("Login...");
		return true;
	}
	
	public Outgoing createOutgoingCall () {
		Outgoing out = new Outgoing();
		return out;
	}
	
	public boolean call(String dest) {
		Outgoing out = createOutgoingCall();
		return out.call(dest);
	}
	
	private void logDebug(String... strs) {
		if (this.debug) {
			System.out.println(strs);
		}
	}
	private boolean initLib() {
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
		
		if (eventListener == null) {
			eventListener = new BackendListener(this.debug);
		}
		plivo.setCallbackObject(eventListener);
		
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
}
