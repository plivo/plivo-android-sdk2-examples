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
	@Override
	public void onStarted(String msg) {
		System.out.println("MyCallback onStarted : " + msg);
	}
	
	@Override
	public void onStopped(int restart) {
		System.out.println("MyCallback onStopped: " + restart);
	}
	
	@Override
	public void onLogin() {
		System.out.println("MyCallback onLogin");
	}
	
	@Override
	public void onLoginFailed() {
		System.out.println("MyCallback onLoginFailed");
	}
	
	@Override
	public void onDebugMessage(String message) {
		System.out.println("[onDebugMessage]:" + message);
	}
	
	@Override
	public void privOnIncomingCall(int pjsuaCallId, String callId, String fromContact, String toContact) {
		Incoming inc = new Incoming(pjsuaCallId, callId, fromContact, toContact);
		if (eventListener != null)
			eventListener.onIncomingCall(inc);
	}
	
	@Override
	public void privOnOutgoingCall(int pjsuaCallId, String callId) {
		
	}
}
