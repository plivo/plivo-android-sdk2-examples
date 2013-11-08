package com.plivo.endpoint;

public interface EventListener {
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
}
