package com.plivo.endpoint;

import com.plivo.endpoint.backend.PlivoAppCallback;

public class EventListener extends PlivoAppCallback{
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
}
