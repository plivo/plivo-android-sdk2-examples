#include <pjsua-lib/pjsua.h>
#include <pjsua-lib/pjsua_internal.h>
#include "plivo_app_callback.h"
//#include "../../pjsua_app.h"
//#include "../../pjsua_app_config.h"

#if defined(PJ_ANDROID) && PJ_ANDROID != 0

#define SIP_DOMAIN "phone.plivo.com"

static PlivoAppCallback* callbackObj = NULL;
//static pjsua_app_cfg_t android_app_config;
static int restart_argc;
static char **restart_argv;

extern const char *pjsua_app_def_argv[];

#define THIS_FILE	"pjsua_app_callback.cpp"

typedef enum {
	_PLIVOUA_INIT_FAILED,
	_PLIVOUA_TRANSPORT_CREATE_FAILED,
	_PLIVOUA_CREATE_FAILED,
	_PLIVOUA_START_FAILED,
	_PLIVOUA_ACC_ADD_FAILED,
	_PLIVOUA_LOGOUT_FAILED,
	_PLIVOUA_MUTE_FAILED,
	_PLIVOUA_UNMUTE_FAILED,
	_PLIVOUA_UNKNOWN_ERROR = -100
}plivoua_error_t;

/**
 * user_data as describe here :
 * http://www.pjsip.org/pjsip/docs/html/structpjsua__acc__config.htm#af6d109091c7130496c6014750f2c9216
 * We use it to save account id
 */
struct my_userdata {
    pjsua_acc_id acc_id;
};

/* pjsua app config */
static pjsua_config app_cfg;
static pjsua_logging_config log_cfg;
static pjsua_transport_config trans_cfg;
static pjsua_acc_id acc_id;
static pjsua_call_id outCallId;
static pjsua_media_config      media_cfg;
static pj_pool_t *app_pool;
static int is_logged_in = 0;

/* global static variable */
static pjsua_call_id incCallId;

/*
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
*/

/**
 * Check if account (pjsua_acc) with id acc_id is registered.
 * return value:
 *  1  : yes
 *  0  : no
 *  -1 : dont know
 */
static int is_registered(pjsua_acc_id acc_id)
{
    int i = 0;
    struct pjsua_data *pjdata = pjsua_get_var();

    for (i = 0; i < pjdata->acc_cnt; i++) {
        pjsua_acc acc = pjdata->acc[i];
        
        //check if this is our account & check registration status
        struct my_userdata  *userdata = (struct my_userdata *)pjsua_acc_get_user_data(acc_id);
        if (acc_id == userdata->acc_id) {
            if (acc.regc == NULL) {
                return 0;
            } else {
                return 1;
            }
        } else {
            continue;
        }
    }
    return -1;
}


static void on_incoming_call(pjsua_acc_id acc_id, pjsua_call_id call_id,pjsip_rx_data *rdata)
{
	pjsua_call_info info;

	pjsua_call_get_info(call_id, &info);

    callbackObj->onDebugMessage("onIncomingCall");
	
	const char *fromContact = pj_strbuf(&info.remote_info);
	const char *toContact = pj_strbuf(&info.local_contact);
	const char *sipCallId = pj_strbuf(&info.call_id);
	callbackObj->onIncomingCall(call_id, sipCallId, fromContact, toContact);
}

static void on_call_media_state(pjsua_call_id call_id) {
    pjsua_call_info call_info;
    pjsua_call_get_info(call_id, &call_info);
    
    callbackObj->onDebugMessage("on_call_media_state");
    // Connecting audio here
    if (call_info.media_status == PJSUA_CALL_MEDIA_ACTIVE) {
    	callbackObj->onDebugMessage("media active");
        pjsua_conf_connect(call_info.conf_slot, 0);
        pjsua_conf_connect(0, call_info.conf_slot);
    }
}

static void on_reg_state(pjsua_acc_id acc_id)
{
    PJ_UNUSED_ARG(acc_id);
    
    pjsua_acc_info acc_info;
    pjsua_acc_get_info(acc_id, &acc_info);

    if (acc_info.status == PJSIP_SC_OK && is_logged_in == 0){
		is_logged_in = 1;
    	callbackObj->onLogin();
    }
	else if (is_logged_in == 1 && is_registered(acc_id) == 0) {
		is_logged_in = 0;
		callbackObj->onLogout();
	}
    else if (PJSIP_IS_STATUS_IN_CLASS(acc_info.status, 400)) {
    	callbackObj->onLoginFailed();
    }
    // Internet is not available
    else if (acc_info.status == 502) {
    	callbackObj->onLoginFailed();
    	callbackObj->onDebugMessage("internet is not available");
    } else {
		char buf[1000];
		sprintf(buf, "unhandled on_reg_state.status=%d", acc_info.status);
    	callbackObj->onDebugMessage(buf);
	}
}
static void on_call_state(pjsua_call_id call_id, pjsip_event *e) {
    PJ_UNUSED_ARG(e);

    pjsua_call_info call_info;

    pjsua_call_get_info(call_id, &call_info);
    pjsua_acc_id acc_id = call_info.acc_id;
    if (call_info.role != PJSIP_ROLE_UAC) {
        // Send out all incoming notifications
        // Check if the state is disconnected and the last status code, in
        // this case, incoming reject event will be sent
        if (call_info.state == PJSIP_INV_STATE_DISCONNECTED && call_info.last_status == 487) {
            // Send incoming reject
			callbackObj->onIncomingCallRejected(call_id, pj_strbuf(&call_info.call_id));
        }
        else if(call_info.state == PJSIP_INV_STATE_DISCONNECTED && call_info.last_status == 200) {
            // Send incoming hangup
			callbackObj->onIncomingCallHangup(call_id, pj_strbuf(&call_info.call_id));
        } else {
			callbackObj->onDebugMessage("onCall : unknown incoming call state");
		}
    }
    else {
          if (call_info.state == PJSIP_INV_STATE_CALLING) {
			  callbackObj->onOutgoingCall(call_id, pj_strbuf(&call_info.call_id));
          }
		  else if (call_info.state == PJSIP_INV_STATE_EARLY) {
        	  callbackObj->onDebugMessage("onCallRinging");
			  callbackObj->onOutgoingCallRinging(call_id, pj_strbuf(&call_info.call_id));
          }
          // Notify the outbound call being answered.
		  else if (call_info.state == PJSIP_INV_STATE_CONFIRMED) {
			  callbackObj->onOutgoingCallAnswered(call_id, pj_strbuf(&call_info.call_id));
          }

          // Call canceled or timeout from the other side before answering
		  else if (call_info.state == PJSIP_INV_STATE_DISCONNECTED && call_info.last_status == 486) {
			  callbackObj->onDebugMessage("onCallDisconnected or timeout");
			  callbackObj->onOutgoingCallRejected(call_id, pj_strbuf(&call_info.call_id));
          }

          // Check if the number is invalid
		  else if (call_info.state == PJSIP_INV_STATE_DISCONNECTED && call_info.last_status == 404) {
        	  callbackObj->onDebugMessage("onCallInvalid");
			  callbackObj->onOutgoingCallInvalid(call_id, pj_strbuf(&call_info.call_id));
          }

          // Call disconnected after answering
		  else if (call_info.state == PJSIP_INV_STATE_DISCONNECTED && call_info.last_status == 200) {
			  callbackObj->onOutgoingCallHangup(call_id, pj_strbuf(&call_info.call_id));
          } else {
        	  callbackObj->onDebugMessage("onCall : unknown outgoing call state");
		  }
    }
}
                                                                             

/**
 * Login to plivo cloud.
 */
int Login(char *username, char *password) {
	pj_status_t status;
	char sipUri[500];
	
	pjsua_acc_config cfg;
	pjsua_acc_config_default(&cfg);

	sprintf(sipUri, "sip:%s@%s;transport=tcp", username, SIP_DOMAIN);
	cfg.id = pj_str(sipUri);
	
	cfg.reg_uri = pj_str("sip:" SIP_DOMAIN);
	cfg.cred_count = 1;
	cfg.cred_info[0].realm = pj_str(SIP_DOMAIN);
	cfg.cred_info[0].scheme = pj_str("digest");
	cfg.cred_info[0].username = pj_str(username);
	cfg.cred_info[0].data_type = PJSIP_CRED_DATA_PLAIN_PASSWD;
	cfg.cred_info[0].data = pj_str(password);
	cfg.proxy[cfg.proxy_cnt++] = pj_str("sip:" SIP_DOMAIN ";transport=tcp");
	
	cfg.reg_timeout = 600;
	cfg.user_data = &acc_id;
	status = pjsua_acc_add(&cfg, PJ_TRUE, &acc_id);
	
	if (status != PJ_SUCCESS) {
		return _PLIVOUA_ACC_ADD_FAILED;
	} else {
		struct my_userdata *userdata = (struct my_userdata *)pj_pool_alloc(app_pool,sizeof(struct my_userdata));
        userdata->acc_id = acc_id;
        pjsua_acc_set_user_data(acc_id, (void *)userdata);
	}
	return 0;
}

/**
 * Logout
 */
int Logout() {
	pj_status_t status;
	status = pjsua_acc_set_registration(acc_id, PJ_FALSE);
	if (status != PJ_SUCCESS) {
		return _PLIVOUA_LOGOUT_FAILED;
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

	app_pool = pjsua_pool_create("plivo-android-sdk", 1000, 1000);

	pjsua_logging_config_default(&log_cfg);
	log_cfg.level = 0;
	log_cfg.console_level = 0;
	
	pjsua_media_config_default(&media_cfg);
	media_cfg.clock_rate = 8000;

	/* Set sound device latency */
	if (PJMEDIA_SND_DEFAULT_REC_LATENCY > 0)
		media_cfg.snd_rec_latency = PJMEDIA_SND_DEFAULT_REC_LATENCY;
	if (PJMEDIA_SND_DEFAULT_PLAY_LATENCY)
		media_cfg.snd_play_latency = PJMEDIA_SND_DEFAULT_PLAY_LATENCY;
	
	app_cfg.cb.on_reg_state = &on_reg_state;
	app_cfg.cb.on_call_state = &on_call_state;
	app_cfg.cb.on_incoming_call = &on_incoming_call;
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

	rc = initPjsua();
	if (rc != 0) {
		return rc;
	}
	
	callbackObj->onStarted("onStarted");
	return 0;
}

/**
 * Call SIP URI
 */
int Call(char *dest)
{
	const pj_str_t dst_uri = pj_str(dest);
	pjsua_call_make_call(acc_id, &dst_uri, 0, NULL, NULL, &outCallId);
}

int Answer(int pjsuaCallId) {
	pjsua_call_answer(pjsuaCallId, 200, NULL, NULL);
}

int Hangup(int pjsuaCallId) {
	pjsua_call_hangup(pjsuaCallId, 0, NULL, NULL);
}

int Reject(int pjsuaCallId) {
	pjsua_call_answer(pjsuaCallId, 486, NULL, NULL);
}

int SendDTMF(int pjsuaCallId, char *digit) {
	pj_str_t dtmfStr = pj_str(digit);
	pjsua_call_dial_dtmf(pjsuaCallId, &dtmfStr);
}

int Mute(int pjsuaCallId) {
	 pjsua_call_info call_info;
	 pjsua_call_get_info(pjsuaCallId, &call_info);
	 if (call_info.conf_slot != PJSUA_INVALID_ID){
		 pjsua_conf_disconnect(0, call_info.conf_slot);
		 return 0;
	 }
	 return _PLIVOUA_MUTE_FAILED; 
}

int UnMute(int pjsuaCallId) {
	 pjsua_call_info call_info;
	 pjsua_call_get_info(pjsuaCallId, &call_info);
	 pjsua_conf_connect(0, call_info.conf_slot);
	 return 0;
}

void plivoDestroy()
{
    //pjsua_app_destroy();

    /** This is on purpose **/
    //pjsua_app_destroy();
}

int plivoRestart()
{
    pj_status_t status;

    plivoDestroy();

    return 0;// initMain(restart_argc, restart_argv);
}

void setCallbackObject(PlivoAppCallback* callback)
{
    callbackObj = callback;
}


#endif
