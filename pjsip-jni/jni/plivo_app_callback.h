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
};

extern "C" {
int plivoStart();
void plivoDestroy();
int plivoRestart();
int Call(char *dest);
int Login(char *username, char *password);
int Answer(int pjsuaCallId);
void setCallbackObject(PlivoAppCallback* callback);
}

#endif /* __PLIVO_APP_CALLBACK_H__ */
