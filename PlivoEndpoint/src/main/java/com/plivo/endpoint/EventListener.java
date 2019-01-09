package com.plivo.endpoint;

public interface EventListener {
	public void onLogin();
	public void onLogout();
	public void onLoginFailed();
	public void onIncomingDigitNotification(String digit);
	/**
	 * This event will be fired when there is new incoming call.
	 * @param incoming new Incoming call object.
	 */
	public void onIncomingCall(Incoming incoming);
	public void onIncomingCallHangup(Incoming incoming);
	public void onIncomingCallRejected(Incoming incoming);
	
	/**
	 * This event will be fired when outgoing call is initiated.
	 * @param outgoing
	 */
	public void onOutgoingCall(Outgoing outgoing);
	
	public void onOutgoingCallAnswered(Outgoing outgoing);
	public void onOutgoingCallRejected(Outgoing outgoing);
	public void onOutgoingCallHangup(Outgoing outgoing);
	public void onOutgoingCallInvalid(Outgoing outgoing);
}

