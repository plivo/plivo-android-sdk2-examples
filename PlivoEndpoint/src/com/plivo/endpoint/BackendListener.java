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
	
	private Incoming curIncoming;
	
	private boolean isLoggedIn;
	
	public BackendListener(boolean debug, Endpoint endpoint, EventListener eventListener) {
		super();
		this.debug = Global.DEBUG;
		//this.debug = "true";
		this.endpoint = endpoint;
		this.eventListener = eventListener;
		this.isLoggedIn = false;
	}
	private void logDebug(String str) {

		if (this.debug == true) {	
			System.out.println("[backend]" + str);
		}
	}
	@Override
	public void onStarted(String msg) {
		//logDebug("onStarted : " + msg);
	}
	
	@Override
	public void onStopped(int restart) {
		//logDebug("onStopped: " + restart);
	}
	
	@Override
	public void onLogin() {
		logDebug("onLogin");
		if (!this.isLoggedIn) {
			if(eventListener != null) {
				eventListener.onLogin();
			}
			this.isLoggedIn = true;
			this.endpoint.setRegistered(true);
		}
	}
	
	@Override
	public void onLogout() {
		logDebug("onLogout");
		this.isLoggedIn = false;
		this.endpoint.setRegistered(false);
		if (eventListener != null) {
			eventListener.onLogout();
		}
	}
	
	@Override
	public void onLoginFailed() {
		logDebug("onLoginFailed");
		this.isLoggedIn = false;
		if (eventListener != null) {
			eventListener.onLoginFailed();
		}
	}
	
	@Override
	public void onDebugMessage(String message) {
		logDebug("[onDebugMessage]" + message);
	}
	
	@Override
	public void onIncomingCall(int pjsuaCallId, String callId, String fromContact, String toContact) {
		logDebug("onIncomingCall");
		
		this.curIncoming = new Incoming(pjsuaCallId, callId, fromContact, toContact);
		
		if (eventListener != null)
			eventListener.onIncomingCall(this.curIncoming);
	}
	
	@Override
	public void onIncomingCallHangup(int pjsuaCallId, String callId) {
		if (eventListener != null)
			eventListener.onIncomingCallHangup(this.curIncoming);
	}
	
	@Override
	public void onIncomingCallRejected(int pjsuaCallId, String callId) {
		if (eventListener != null)
			eventListener.onIncomingCallRejected(this.curIncoming);
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
	@Override
	public void onOutgoingCallAnswered(int pjsuaCallId, String callId) {
		if (eventListener != null) {
			eventListener.onOutgoingCallAnswered(this.curOutgoing);
		}
	}
	
	@Override
	public void onOutgoingCallHangup(int pjsuaCallId, String callId) {
		if (eventListener != null) {
			eventListener.onOutgoingCallHangup(this.curOutgoing);
		}
	}
	@Override
	public void onOutgoingCallRejected(int pjsuaCallId, String callId) {
		if (eventListener != null) {
			eventListener.onOutgoingCallRejected(this.curOutgoing);
		}
	}
	@Override
	public void onOutgoingCallInvalid(int pjsuaCallId, String callId) {
		if (eventListener != null) {
			eventListener.onOutgoingCallInvalid(this.curOutgoing);
		}
	}
}

