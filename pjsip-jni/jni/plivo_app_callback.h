#ifndef __PLIVO_APP_CALLBACK_H__
#define __PLIVO_APP_CALLBACK_H__
class PlivoAppCallback {
public:
    virtual ~PlivoAppCallback() {}
    virtual void onStarted(const char *msg) {}
    virtual void onStopped(int restart) {}
    virtual void onLogin(){}
    virtual void onLoginFailed(){}
    virtual void onDebugMessage(const char *msg){}
	//FIXME : events below should be private event
	virtual void privOnIncomingCall(int callId, const char *sipCallId, const char *fromContact, const char *toContact){}
	
	//on outgoing call events
	virtual void privOnOutgoingCall(int callId){}
	virtual void privOnOutgoingCallRinging(int callId){}
	virtual void privOnOutgoingCallRejected(int callId){}
	virtual void privOnOutgoingCallAnswered(int callId){}
	virtual void privOnOutgoingCallHangup(int callId){}
	virtual void privOnOutgoingCallInvalid(int callId){}
};

extern "C" {
int plivoStart();
void plivoDestroy();
int plivoRestart();

/** Make a call */
int Call(char *dest);

/* Login */
int Login(char *username, char *password);

/* Answer a call */
int Answer(int pjsuaCallId);

/* Hangup a call */
int Hangup(int pjsuaCallId);

/* Reject a call */
int Reject(int pjsuaCallId);

void setCallbackObject(PlivoAppCallback* callback);
}

#endif /* __PLIVO_APP_CALLBACK_H__ */
