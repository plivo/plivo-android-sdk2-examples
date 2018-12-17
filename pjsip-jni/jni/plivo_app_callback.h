#ifndef __PLIVO_APP_CALLBACK_H__
#define __PLIVO_APP_CALLBACK_H__

#include <map>
#include <string>


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
	virtual void onIncomingCall(int callId, const char *sipCallId, const char *fromContact, const char *toContact, const char * header){}
	virtual void onIncomingCallHangup(int callId, const char *sipCallId){}
	virtual void onIncomingCallRejected(int callId, const char *sipCallId){}
	
	//on outgoing call events
	virtual void onOutgoingCall(int callId, const char *sipCallId){}
	virtual void onOutgoingCallRinging(int callId, const char *sipCallId){}
	virtual void onOutgoingCallRejected(int callId, const char *sipCallId){}
	virtual void onOutgoingCallAnswered(int callId, const char *sipCallId){}
	virtual void onOutgoingCallHangup(int callId, const char *sipCallId){}
	virtual void onOutgoingCallInvalid(int callId, const char *sipCallId){}
	virtual void onIncomingDigitNotification(int digit){}
};

extern "C" {
int plivoStart();
void plivoDestroy();
int plivoRestart();

/** Make a call */
int Call(char *dest);

/** Make a call with headers*/
int CallH(char *dest, char *headers);

/* Login */
int Login(char *username, char *password, int regTimeout);

int Logout();

void setRegTimeout(int regTimeout);

void LoginAgain();

void keepAlive();
void resetEndpoint();

/* Answer a call */
int Answer(int pjsuaCallId);

/* Hangup a call */
int Hangup(int pjsuaCallId);

/* Reject a call */
int Reject(int pjsuaCallId);

/* send digit */
int SendDTMF(int pjsuaCallId, char *digit);

/* mute */
int Mute(int pjsuaCallId);

/* unmute */
int UnMute(int pjsuaCallId);

/* hold */
int Hold(int pjsuaCallId);

/* unhold */
int UnHold(int pjsuaCallId);

int isRegistered();

void setCallbackObject(PlivoAppCallback* callback);

void registerToken(char *deviceToken);

void relayVoipPushNotification(char *pushMessage);

}

#endif /* __PLIVO_APP_CALLBACK_H__ */
