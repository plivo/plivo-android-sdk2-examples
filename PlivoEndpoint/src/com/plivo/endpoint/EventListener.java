package com.plivo.endpoint;

public interface EventListener {
	public void onLogin();
	public void onLoginFailed();
	/**
	 * This event will be fired when there is new incoming call.
	 * @param incoming new Incoming call object.
	 */
	public void onIncomingCall(Incoming incoming);
	
	/**
	 * This event will be fired when outgoing call is initiated.
	 * @param outgoing
	 */
	public void onOutgoingCall(Outgoing outgoing);
	
	public void onOutgoingCallAnswered(Outgoing outgoing);
	
	public void onOutgoingCallHangup(Outgoing outgoing);
}
