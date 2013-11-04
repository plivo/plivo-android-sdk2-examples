#include <pjsua-lib/pjsua.h>
#include "plivo_app_callback.h"
#include "../../pjsua_app.h"
#include "../../pjsua_app_config.h"

#if defined(PJ_ANDROID) && PJ_ANDROID != 0

static PlivoAppCallback* registeredCallbackObject = NULL;
static pjsua_app_cfg_t android_app_config;
static int restart_argc;
static char **restart_argv;

extern const char *pjsua_app_def_argv[];

#define THIS_FILE	"pjsua_app_callback.cpp"

/* pjsua app config */
static pjsua_config app_cfg;
static pjsua_logging_config log_cfg;
static pjsua_transport_config trans_cfg;
static pjsua_acc_id acc_id;
static pjsua_call_id outCallId;
pjsua_media_config      media_cfg;

/** Callback wrapper **/
void on_cli_started(pj_status_t status, const char *msg)
{
    char errmsg[PJ_ERR_MSG_SIZE];
    if (registeredCallbackObject) {
	if ((status != PJ_SUCCESS) && (!msg || !*msg)) {
	    pj_strerror(status, errmsg, sizeof(errmsg));
	    msg = errmsg;
	}
	registeredCallbackObject->onStarted(msg);
    }
}

void on_cli_stopped(pj_bool_t restart, int argc, char **argv)
{
    if (restart) {
	restart_argc = argc;
	restart_argv = argv;
    }

    if (registeredCallbackObject) {
	registeredCallbackObject->onStopped(restart);
    }
}

static int initMain(int argc, char **argv)
{
    pj_status_t status;
    android_app_config.argc = argc;
    android_app_config.argv = argv;

    status = pjsua_app_init(&android_app_config);
    if (status == PJ_SUCCESS) {
	status = pjsua_app_run(PJ_FALSE);
    } else {
	pjsua_app_destroy();
    }

    return status;
}

#define _PLIVOUA_INIT_FAILED -102
#define _PLIVOUA_TRANSPORT_CREATE_FAILED -1003
#define _PLIVOUA_CREATE_FAILED -104
#define _PLIVOUA_START_FAILED -105
#define _PLIVOUA_ACC_ADD_FAILED -106

static void on_incoming_call(pjsua_acc_id acc_id, pjsua_call_id call_id,pjsip_rx_data *rdata)
{
}

static void on_call_media_state(pjsua_call_id call_id) {
    pjsua_call_info call_info;
    pjsua_call_get_info(call_id, &call_info);
    
    registeredCallbackObject->onDebugMessage("on_call_media_state");
    // Connecting audio here
    if (call_info.media_status == PJSUA_CALL_MEDIA_ACTIVE) {
    	registeredCallbackObject->onDebugMessage("media active");
        pjsua_conf_connect(call_info.conf_slot, 0);
        pjsua_conf_connect(0, call_info.conf_slot);
    }
}

static void on_reg_state(pjsua_acc_id acc_id)
{
    PJ_UNUSED_ARG(acc_id);
    
    pjsua_acc_info acc_info;
    pjsua_acc_get_info(acc_id, &acc_info);
    
    if (acc_info.status == PJSIP_SC_OK){
    	registeredCallbackObject->onLogin();
    }
    else if (PJSIP_IS_STATUS_IN_CLASS(acc_info.status, 400)) {
    	registeredCallbackObject->onLoginFailed();
    	registeredCallbackObject->onDebugMessage("Registration failed");
    }
    // Internet is not available
    else if (acc_info.status == 502) {
    	registeredCallbackObject->onLoginFailed();
    	registeredCallbackObject->onDebugMessage("internet is not available");
    }
}
static void on_call_state(pjsua_call_id call_id, pjsip_event *e) {
    PJ_UNUSED_ARG(e);

    pjsua_call_info call_info;

    pjsua_call_get_info(call_id, &call_info);
    pjsua_acc_id acc_id = call_info.acc_id;
    registeredCallbackObject->onDebugMessage("onCallState");
    if (0) {
        // Send out all incoming notifications
        // Check if the state is disconnected and the last status code, in
        // this case, incoming reject event will be sent
        if (call_info.state == PJSIP_INV_STATE_DISCONNECTED && call_info.last_status == 487) {
            // Send incoming reject
            //PlivoIncoming *incoming = plivo_incoming_object(acc_id, call_id);
            //[PlivoClientObject onIncomingCallRejectedNotification:incoming];
        }
        else if(call_info.state == PJSIP_INV_STATE_DISCONNECTED && call_info.last_status == 200) {
            // Send incoming hangup
            //PlivoIncoming *incoming = plivo_incoming_object(acc_id, call_id);
            //[PlivoClientObject onIncomingCallHangupNotification:incoming];
        }
    }
    else {
          if (call_info.state == PJSIP_INV_STATE_CALLING) {
        	  registeredCallbackObject->onDebugMessage("onCalling");
          }
		  else if (call_info.state == PJSIP_INV_STATE_EARLY) {
        	  registeredCallbackObject->onDebugMessage("onCallRinging");
          }
          // Notify the outbound call being answered.
		  else if (call_info.state == PJSIP_INV_STATE_CONFIRMED) {
        	  registeredCallbackObject->onDebugMessage("onCallAnswered");
          }

          // Call canceled or timeout from the other side before answering
		  else if (call_info.state == PJSIP_INV_STATE_DISCONNECTED && call_info.last_status == 486) {
			  registeredCallbackObject->onDebugMessage("onCallDisconnected or timeout");
          }

          // Check if the number is invalid
		  else if (call_info.state == PJSIP_INV_STATE_DISCONNECTED && call_info.last_status == 404) {
        	  registeredCallbackObject->onDebugMessage("onCallAnswered");
          }

          // Call disconnected after answering
		  else if (call_info.state == PJSIP_INV_STATE_DISCONNECTED && call_info.last_status == 200) {
        	  registeredCallbackObject->onDebugMessage("onCallHangup");
          } else {
        	  registeredCallbackObject->onDebugMessage("onCall : unknown state");
		  }
    }
}
                                                                             
#define SIP_USER "1001"
#define SIP_DOMAIN "fs1.labhijau.net"
#define SIP_PASSWD "7654"

int plivoLogin() {
	pj_status_t status;
	
	pjsua_acc_config cfg;
	pjsua_acc_config_default(&cfg);
	 
	cfg.id = pj_str("sip:" SIP_USER "@" SIP_DOMAIN";transport=tcp");
	cfg.reg_uri = pj_str("sip:" SIP_DOMAIN);
	cfg.cred_count = 1;
	cfg.cred_info[0].realm = pj_str(SIP_DOMAIN);
	cfg.cred_info[0].scheme = pj_str("digest");
	cfg.cred_info[0].username = pj_str(SIP_USER);
	cfg.cred_info[0].data_type = PJSIP_CRED_DATA_PLAIN_PASSWD;
	cfg.cred_info[0].data = pj_str(SIP_PASSWD);
	cfg.proxy[cfg.proxy_cnt++] = pj_str("sip:" SIP_DOMAIN ";transport=tcp");
	 
	status = pjsua_acc_add(&cfg, PJ_TRUE, &acc_id);
	
	if (status != PJ_SUCCESS) {
		return _PLIVOUA_ACC_ADD_FAILED;
	}
	return 0;
}

static int initPjsua() {
    pj_status_t status;
	
	status = pjsua_create();
	if (status != PJ_SUCCESS) {
		fprintf(stderr,"pjsua_create failed\n");
		return _PLIVOUA_CREATE_FAILED;
	}

	pjsua_config_default(&app_cfg);
	
	pjsua_logging_config_default(&log_cfg);
	log_cfg.level = 4;
	log_cfg.console_level = 4;
	
	pjsua_media_config_default(&media_cfg);
	media_cfg.clock_rate = 8000;
	
	app_cfg.cb.on_reg_state = &on_reg_state;
	app_cfg.cb.on_call_state = &on_call_state;
	app_cfg.cb.on_call_media_state = &on_call_media_state;

	status = pjsua_init(&app_cfg, &log_cfg, &media_cfg);
	if (status != PJ_SUCCESS) {
		fprintf(stderr, "plivoua_init failed");
		return _PLIVOUA_INIT_FAILED;
	}

	pjsua_transport_config_default(&trans_cfg);
	pjsua_transport_id tid = -1;
	status = pjsua_transport_create(PJSIP_TRANSPORT_TCP, &trans_cfg, &tid);
	if (status != PJ_SUCCESS) {
		return _PLIVOUA_TRANSPORT_CREATE_FAILED;
	}
	
	status = pjsua_start();
	if (status != PJ_SUCCESS) {
		return _PLIVOUA_START_FAILED;
	}
	
	return 0;
}

int plivoStart()
{
    pj_status_t status;
	int rc;

    const char **argv = pjsua_app_def_argv;
    int argc = pjsua_app_def_argc;

    pj_bzero(&android_app_config, sizeof(android_app_config));

    android_app_config.on_started = &on_cli_started;
    android_app_config.on_stopped = &on_cli_stopped;

    //return initMain(argc, (char**)argv);
	rc = initPjsua();
	if (rc != 0) {
		return rc;
	}
	
	rc = plivoLogin();
	if (rc != 0) {
		return rc;
	}
	registeredCallbackObject->onStarted("wataw");
	return 0;
}

int Call()
{
	const pj_str_t dst_uri = pj_str("sip:5000@fs1.labhijau.net");
	pjsua_call_make_call(acc_id, &dst_uri, 0, NULL, NULL, &outCallId);
}
void plivoDestroy()
{
    pjsua_app_destroy();

    /** This is on purpose **/
    pjsua_app_destroy();
}

int plivoRestart()
{
    pj_status_t status;

    plivoDestroy();

    return initMain(restart_argc, restart_argv);
}

void setCallbackObject(PlivoAppCallback* callback)
{
    registeredCallbackObject = callback;
}


#endif
