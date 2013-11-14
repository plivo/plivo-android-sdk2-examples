#ifndef __PLIVO_APP_CALLBACK_H__
#define __PLIVO_APP_CALLBACK_H__
class PlivoAppCallback {
public:
    virtual ~PlivoAppCallback() {}
    virtual void onStarted(const char *msg) {}
    virtual void onStopped(int restart) {}
    virtual void onLogin(){}
    virtual void onLogout(){}
    virtual void onLoginFailed(){}
    virtual void onDebugMessage(const char *msg){}
	//FIXME : events below should be private event
	virtual void onIncomingCall(int callId, const char *sipCallId, const char *fromContact, const char *toContact){}
	virtual void onIncomingCallHangup(int callId, const char *sipCallId){}
	virtual void onIncomingCallRejected(int callId, const char *sipCallId){}
	
	//on outgoing call events
	virtual void onOutgoingCall(int callId, const char *sipCallId){}
	virtual void onOutgoingCallRinging(int callId, const char *sipCallId){}
	virtual void onOutgoingCallRejected(int callId, const char *sipCallId){}
	virtual void onOutgoingCallAnswered(int callId, const char *sipCallId){}
	virtual void onOutgoingCallHangup(int callId, const char *sipCallId){}
	virtual void onOutgoingCallInvalid(int callId, const char *sipCallId){}
};

extern "C" {
int plivoStart();
void plivoDestroy();
int plivoRestart();

/** Make a call */
int Call(char *dest);

/* Login */
int Login(char *username, char *password);

int Logout();

/* Answer a call */
int Answer(int pjsuaCallId);

/* Hangup a call */
int Hangup(int pjsuaCallId);

/* Reject a call */
int Reject(int pjsuaCallId);

void setCallbackObject(PlivoAppCallback* callback);
}

#endif /* __PLIVO_APP_CALLBACK_H__ */
