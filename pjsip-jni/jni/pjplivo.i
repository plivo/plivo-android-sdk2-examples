%module (directors="1") plivo

%{
#include "plivo_app_callback.h"
#include "../../pjsua_app.h"

#ifdef __cplusplus
extern "C" {
#endif
	int plivoStart();
	void plivoDestroy();
	int plivoRestart();
	void setCallbackObject(PlivoAppCallback* callback);	
#ifdef __cplusplus
}
#endif
%}

int plivoStart();
void plivoDestroy();
int plivoRestart();

/* turn on director wrapping PlivoAppCallback */
%feature("director") PlivoAppCallback;

%include "plivo_app_callback.h"

void setCallbackObject(PlivoAppCallback* callback);

