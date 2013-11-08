package com.plivo.endpoint;

public interface EventListener {
	/**
	 * This event will be fired when there is new incoming call.
	 * @param incoming new Incoming call object.
	 */
	public void onIncomingCall(Incoming incoming);
}
