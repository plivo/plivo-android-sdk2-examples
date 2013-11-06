package com.plivo.endpoint;

import com.plivo.endpoint.backend.plivo;


public class Endpoint {
	private EventListener eventHandler;
	private boolean initialized = false;
	
	private Endpoint() {
		if (initLib() == true) {
			initialized = true;
		}
	}
	
	public static Endpoint newInstance() {
		Endpoint endpoint = new Endpoint();
		if (endpoint.initialized == false) {
			return null;
		}
		return endpoint;
	}
	
	public boolean login(String username, String password) {
		System.out.println("Try to login");
		if (plivo.Login(username, password) != 0) {
			System.out.println("Login attempt failed");
			return false;
		}
		System.out.println("Login...");
		return true;
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
		
		if (eventHandler == null) {
			eventHandler = new EventListener();
		}
		plivo.setCallbackObject(eventHandler);
		
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
