package com.plivo.endpoint;

import com.plivo.endpoint.backend.PlivoAppCallback;

public class BackendListener extends PlivoAppCallback{
	/**
	 * Debug flag.
	 */
	private boolean debug;
	
	private Endpoint endpoint;
	
	/**
	 * EventListener interface that need to be implemented by user.
	 */
	private EventListener eventListener;
	
	/**
	 * Current outgoing call.
	 */
	private Outgoing curOutgoing;
	
	public BackendListener(boolean debug, Endpoint endpoint, EventListener eventListener) {
		super();
		this.debug = debug;
		this.endpoint = endpoint;
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
		logDebug("[onDebugMessage]" + message);
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
		Outgoing out = this.endpoint.getOutgoing();
		this.curOutgoing= out;
		out.pjsuaCallId = pjsuaCallId;
		out.setCallId(callId);
		if (eventListener != null) {
			eventListener.onOutgoingCall(out);
		}
	}
}
