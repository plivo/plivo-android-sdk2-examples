package com.plivo.endpoint;

import com.plivo.endpoint.backend.PlivoAppCallback;

public class BackendListener extends PlivoAppCallback{
	private boolean debug;
	private EventListener eventListener;
	public BackendListener(boolean debug, EventListener eventListener) {
		super();
		this.debug = debug;
		this.eventListener = eventListener;
	}
	private void logDebug(String str) {
		if (this.debug) {
			System.out.println("[backend]" + str);
		}
	}
	@Override
	public void onStarted(String msg) {
		logDebug("onStarted : " + msg);
	}
	
	@Override
	public void onStopped(int restart) {
		logDebug("onStopped: " + restart);
	}
	
	@Override
	public void onLogin() {
		logDebug("onLogin");
	}
	
	@Override
	public void onLoginFailed() {
		logDebug("onLoginFailed");
	}
	
	@Override
	public void onDebugMessage(String message) {
		logDebug(message);
	}
	
	@Override
	public void onIncomingCall(int pjsuaCallId, String callId, String fromContact, String toContact) {
		Incoming inc;
		logDebug("onIncomingCall");
		
		inc = new Incoming(pjsuaCallId, callId, fromContact, toContact);
		
		if (eventListener != null)
			eventListener.onIncomingCall(inc);
	}
	
	@Override
	public void onOutgoingCall(int pjsuaCallId, String callId) {
		
	}
}
