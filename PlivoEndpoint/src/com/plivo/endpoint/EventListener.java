package com.plivo.endpoint;

import com.plivo.endpoint.backend.PlivoAppCallback;

public class EventListener extends PlivoAppCallback{
	private boolean debug;
	public EventListener(boolean debug) {
		super();
		this.debug = debug;
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
	
	public void onIncomingCall(Incoming inc) {
		System.out.println("onIncomingCall:" + inc.getCallId() + ".from = " + inc.getFromContact());
	}
	
	@Override
	public void privOnIncomingCall(int pjsuaCallId, String callId, String fromContact, String toContact) {
		Incoming inc = new Incoming(pjsuaCallId, callId, fromContact, toContact);
		onIncomingCall(inc);
	}
	
	private void logDebug(String... strs) {
		if (this.debug) {
			System.out.println(strs);
		}
	}
}
