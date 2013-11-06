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
};

extern "C" {
int plivoStart();
void plivoDestroy();
int plivoRestart();
int Call(char *dest);
int Login(char *username, char *password);
void setCallbackObject(PlivoAppCallback* callback);
}

#endif /* __PLIVO_APP_CALLBACK_H__ */
