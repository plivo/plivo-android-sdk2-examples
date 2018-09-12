#include <iostream>
#include <vector>
#include <map>
#include <sstream>


#include <pjsua-lib/pjsua.h>
#include <pjsua-lib/pjsua_internal.h>
#include "plivo_app_callback.h"
#include "pjmedia_audiodev.h"


using namespace std;

#if defined(PJ_ANDROID) && PJ_ANDROID != 0

#define SIP_DOMAIN "phone.plivo.com"

//#define REG_URI "52.9.254.110"

#define PLIVO_ENDPOINT_VER "2.0"

#define MAX_ENDPOINT_LENGTH 212

static PlivoAppCallback* callbackObj = NULL;

#define THIS_FILE   "pjsua_app_callback.cpp"


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
static pjsua_media_config media_cfg;
static pj_pool_t *app_pool;
static int is_logged_in = 0;
static unsigned opt = 2;
static unsigned latency_ms = 0;
static pjmedia_echo_state *ec;
static pjmedia_port *dn_port;

/* global static variable */
static pjsua_call_id incCallId;

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

vector<string> &split(const string &s, char delim, vector<string> &elems) {
    stringstream ss(s);
    string item;
    while (getline(ss, item, delim)) {
        elems.push_back(item);
    }
    return elems;
}


vector<string> split(const string &s, char delim) {
    vector<string> elems;
    split(s, delim, elems);
    return elems;
}

static void log_writer(int level, const char *data, int len)
{
    callbackObj->onDebugMessage(data);

}

static void on_incoming_call(pjsua_acc_id acc_id, pjsua_call_id call_id,pjsip_rx_data *rdata)
{
    pjsua_call_info info;

    char * header = rdata->msg_info.msg_buf;
    string str(header);
    int i,k;
    vector<string> hdr_vec = split(str, '\n');
    string _str_1 = "X-PH";
    string _str_2 = "X-Ph";
    string _header;
    for (i=0; i< hdr_vec.size();i++) {
        size_t pos1 = hdr_vec[i].find(_str_1);
        size_t pos2 = hdr_vec[i].find(_str_2);
        if (pos1 != string::npos || pos2 != string::npos) {
            _header += hdr_vec[i];
            _header += ',';
        }
    }
    if (_header.length() > 0)
        _header.erase(_header.length()-1, 1);
    char * hdr = new char[_header.length() + 1];
    strcpy(hdr, _header.c_str());
    pjsua_call_get_info(call_id, &info);

    callbackObj->onDebugMessage("onIncomingCall");

    const char *fromContact = pj_strbuf(&info.remote_info);
    const char *toContact = pj_strbuf(&info.local_contact);
    const char *sipCallId = pj_strbuf(&info.call_id);
    /* Automatically answer incoming calls with 180/Ringing */
    pjsua_call_answer(call_id, 180, NULL, NULL);
    callbackObj->onIncomingCall(call_id, sipCallId, fromContact, toContact, hdr);
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
        //is_logged_in = 0;
        //pjsua_acc_del(acc_id);
        //callbackObj->onLogout();
    }
    else if (PJSIP_IS_STATUS_IN_CLASS(acc_info.status, 400)) {
        callbackObj->onLoginFailed();
    }
    // Internet is not available
    else if (acc_info.status == 502) {
        callbackObj->onLoginFailed();
        callbackObj->onDebugMessage("internet is not available");
    }else if (acc_info.status == 503) {
         callbackObj->onLoginFailed();
         callbackObj->onDebugMessage("Service unavailable");
     }else if (acc_info.status == 200 && is_logged_in == 1) {

        char buf[1000];
        sprintf(buf, "Token registered successfully=%d", acc_info.status);
        callbackObj->onDebugMessage(buf);

      } else {
        char buf[1000];
        sprintf(buf, "unhandled on_reg_state status is: %d", acc_info.status);
        callbackObj->onLoginFailed();
        callbackObj->onDebugMessage(buf);
    }
}

static void call_on_dtmf_callback(pjsua_call_id call_id, int dtmf){
    pjsua_call_info call_info;
    pjsua_call_get_info(call_id, &call_info);

    int new_dtmf = dtmf - 48;
    callbackObj->onIncomingDigitNotification(new_dtmf);
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
            callbackObj->onDebugMessage("rejection message");
            callbackObj->onIncomingCallRejected(call_id, pj_strbuf(&call_info.call_id));
        }
        else if(call_info.state == PJSIP_INV_STATE_DISCONNECTED && call_info.last_status == 200) {
            // Send incoming hangup
            callbackObj->onDebugMessage("checking message");
            callbackObj->onIncomingCallHangup(call_id, pj_strbuf(&call_info.call_id));
        } else {
            callbackObj->onDebugMessage("onCall : unknown incoming call state");
        }
    }
    else {
          if (call_info.state == PJSIP_INV_STATE_CALLING) {
              callbackObj->onOutgoingCall(call_id, pj_strbuf(&call_info.call_id));
          }
          else if (call_info.state == PJSIP_INV_STATE_EARLY || call_info.last_status == 183 || call_info.last_status == 180) {
              callbackObj->onDebugMessage("onCallRinging");
              callbackObj->onOutgoingCallRinging(call_id, pj_strbuf(&call_info.call_id));
          }
          // Notify the outbound call being answered.
          else if (call_info.state == PJSIP_INV_STATE_CONFIRMED) {
              callbackObj->onOutgoingCallAnswered(call_id, pj_strbuf(&call_info.call_id));
          }

          // Call canceled or timeout from the other side before answering
          else if (call_info.state == PJSIP_INV_STATE_DISCONNECTED  && (call_info.last_status >= 480 && call_info.last_status <= 489)) {
              callbackObj->onDebugMessage("onCallDisconnected or timeout");
              callbackObj->onOutgoingCallRejected(call_id, pj_strbuf(&call_info.call_id));
          }


          // Check if the number is invalid or Timeout
          else if (call_info.state == PJSIP_INV_STATE_DISCONNECTED && (call_info.last_status == 404 || call_info.last_status == 408)) {
              callbackObj->onDebugMessage("onCallInvalid");
              callbackObj->onOutgoingCallInvalid(call_id, pj_strbuf(&call_info.call_id));
          }


          // Call disconnected after answering
          else if (call_info.state == PJSIP_INV_STATE_DISCONNECTED && call_info.last_status == 200) {
              callbackObj->onOutgoingCallHangup(call_id, pj_strbuf(&call_info.call_id));
          }

          else {
              callbackObj->onDebugMessage("onCall : unknown outgoing call state");
              //callbackObj->onOutgoingCallHangup(call_id, pj_strbuf(&call_info.call_id));

          }
    }
}


/**
 * Login to plivo cloud.
 */
int Login(char *username, char *password, int regTimeout) {


    if(is_logged_in == 0){

        if(strlen(username) <= MAX_ENDPOINT_LENGTH){


            pj_status_t status;
            char sipUri[500];

            pjsua_acc_config cfg;
            pjsua_acc_config_default(&cfg);

            sprintf(sipUri, "sip:%s@%s;transport=tls", username, SIP_DOMAIN);
            cfg.id = pj_str(sipUri);

            cfg.reg_uri = pj_str("sip:" SIP_DOMAIN);
            cfg.cred_count = 1;
            cfg.cred_info[0].realm = pj_str(SIP_DOMAIN);
            cfg.cred_info[0].scheme = pj_str("digest");
            cfg.cred_info[0].username = pj_str(username);
            cfg.cred_info[0].data_type = PJSIP_CRED_DATA_PLAIN_PASSWD;
            cfg.cred_info[0].data = pj_str(password);
            cfg.proxy[cfg.proxy_cnt++] = pj_str("sip:" SIP_DOMAIN ";transport=tls");

            cfg.reg_timeout = regTimeout;
            cfg.ka_interval = 0;

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

        }else{

            callbackObj->onDebugMessage("Invalid Endpoint");
            return _PLIVOUA_ACC_ADD_FAILED;
        }

    }else{

        callbackObj->onDebugMessage("Endpoint already registered");
        return 0;

    }

}

/**
 * Logout
 */
int Logout() {

    if (pjsua_acc_get_count()) {

        //Account Deletion
        if (!pjsua_acc_is_valid(acc_id)) {
            callbackObj->onDebugMessage("Invalid Account-ID for deletion");
            return 0;
        } else {

            pj_status_t status;

            struct my_userdata *userdata = (struct my_userdata *)pjsua_acc_get_user_data(acc_id);
            userdata->acc_id = acc_id;
            pjsua_acc_set_user_data(acc_id, (void *)userdata);

            status = pjsua_acc_set_registration(acc_id, PJ_FALSE);
            if (status != PJ_SUCCESS) {
                return _PLIVOUA_LOGOUT_FAILED;
            } else {
                is_logged_in = 0;
                pjsua_acc_del(acc_id);
                callbackObj->onLogout();
            }
            return 0;
        }
    }else{
        callbackObj->onDebugMessage("User not loggedIn");
        return 0;
    }
}

void setRegTimeout(int regTimeout) {
    pjsua_acc_config acc_cfg;

    pjsua_acc_get_config(acc_id, app_pool,&acc_cfg);

    acc_cfg.reg_timeout = regTimeout;
    pj_status_t status = pjsua_acc_modify(acc_id, &acc_cfg);

    if (status != PJ_SUCCESS)
         fprintf(stderr, "Error in updating Registration Timeout Interval");
}

void LoginAgain() {
    pjsua_acc_config acc_cfg;

    pj_status_t status = pjsua_acc_get_config(acc_id, app_pool,&acc_cfg);

    if (status == PJ_SUCCESS) {
        if (is_logged_in == 1 && is_registered(acc_id) == 0) {
            pj_status_t regstatus = pjsua_acc_set_registration(acc_id, PJ_TRUE);
            if (regstatus != PJ_SUCCESS)
                callbackObj->onDebugMessage("Failed to log in again");
	}
    } else {
        callbackObj->onDebugMessage("Error occured while logging in again");
    }
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
    log_cfg.level = 5;
    log_cfg.console_level = 4;
    log_cfg.msg_logging = 1;

    log_cfg.cb = log_writer;

    pjsua_media_config_default(&media_cfg);
    media_cfg.clock_rate = 16000;

    
    /* Set sound device latency */
    if (PJMEDIA_SND_DEFAULT_REC_LATENCY > 0)
        media_cfg.snd_rec_latency = PJMEDIA_SND_DEFAULT_REC_LATENCY;
    if (PJMEDIA_SND_DEFAULT_PLAY_LATENCY)
        media_cfg.snd_play_latency = PJMEDIA_SND_DEFAULT_PLAY_LATENCY;

    app_cfg.cb.on_reg_state = &on_reg_state;
    app_cfg.cb.on_call_state = &on_call_state;
    app_cfg.cb.on_incoming_call = &on_incoming_call;
    app_cfg.cb.on_call_media_state = &on_call_media_state;
    app_cfg.cb.on_dtmf_digit = &call_on_dtmf_callback;

    // Adding plivo User-Agent
    char *str = "PlivoAndroidSDK-v";
    char *userAgent = (char*)calloc(strlen(str)+strlen(PLIVO_ENDPOINT_VER)+1, sizeof(char));
    strcpy(userAgent,str);
    strcat(userAgent,PLIVO_ENDPOINT_VER);
    pj_str_t user_agent = pj_str(userAgent);
    app_cfg.user_agent = user_agent;

    status = pjsua_init(&app_cfg, &log_cfg, &media_cfg);
    if (status != PJ_SUCCESS) {
        fprintf(stderr, "plivoua_init failed");
        return _PLIVOUA_INIT_FAILED;
    }

    pj_log_set_log_func(log_writer);


    media_cfg.audio_frame_ptime = 20;
    media_cfg.channel_count = 0;
    media_cfg.ec_tail_len = 200;
    media_cfg.ec_options = 0;
    media_cfg.no_vad = false;
    media_cfg.quality = 4;
    media_cfg.has_ioqueue = true;

    /* Create echo canceller */
    status = pjsua_set_ec(media_cfg.ec_tail_len, media_cfg.ec_options);
    if (status != PJ_SUCCESS) {
        fprintf(stderr, "Error setting Echo Cancellation");
        return 1;
    }

    pjsua_transport_config_default(&trans_cfg);
    pjsua_transport_id tid = -1;
    status = pjsua_transport_create(PJSIP_TRANSPORT_TLS, &trans_cfg, &tid);
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
    if(strlen(dest) > 0){

        pj_status_t status;

        const pj_str_t dst_uri = pj_str(dest);

        status = pjsua_verify_sip_url(dst_uri.ptr);

        if (status != PJ_SUCCESS)
        {
            callbackObj->onDebugMessage("Error initiating SIP call, Invalid URI");
            return 0;

        }else{

            status = pjsua_call_make_call(acc_id, &dst_uri, 0, NULL, NULL, &outCallId);
            if (status != PJ_SUCCESS)
            {
                callbackObj->onDebugMessage("Error initiating SIP call, Invalid URI");
                return 0;
            }
        }
    }else{

        callbackObj->onDebugMessage("Error initiating SIP call, Invalid URI");
        return 0;

    }
}


int CallH(char *dest, char *headers)
{
    if(strlen(dest) > 0){

            pj_status_t status;
            const pj_str_t dst_uri = pj_str(dest);

            status = pjsua_verify_sip_url(dst_uri.ptr);

            if (status != PJ_SUCCESS)
            {
                callbackObj->onDebugMessage("Error initiating SIP call, Invalid URI");
                return 0;

            }else{


                int i;
                //map<string, string> map_hdr;
                vector<string> key;
                vector<string> value;
                string str(headers);
                char *head;
                char *tail;
                pj_str_t head_pj;
                pj_str_t tail_pj;
                pjsua_msg_data msg_data;
                pjsua_msg_data_init(&msg_data);

                vector<string> hdr_vec = split(str, ',');
                for (i=0; i< hdr_vec.size();i++) {
                    vector<string> each_vec = split(hdr_vec[i], ':');
                    key.push_back(each_vec[0]);
                    value.push_back(each_vec[1]);
                    //map_hdr[each_vec[0]] = each_vec[1];
                }

                int header_length = key.size();
                pjsip_generic_string_hdr CustomHeader[header_length];

                pj_str_t header_pj = pj_str(headers);

                for (i=0; i< hdr_vec.size(); i++) {
                    head = new char[key[i].length() + 1];
                    strcpy(head, key[i].c_str());

                    tail = new char[value[i].length() + 1];
                    strcpy(tail, value[i].c_str());

                    head_pj = pj_str(head);
                    tail_pj = pj_str(tail);
                    pjsip_generic_string_hdr_init2(&CustomHeader[i], &head_pj, &tail_pj);
                    pj_list_push_back(&msg_data.hdr_list, &CustomHeader[i]);
                }


                status = pjsua_call_make_call(acc_id, &dst_uri, 0, NULL, &msg_data, &outCallId);
                if (status != PJ_SUCCESS)
                {
                    callbackObj->onDebugMessage("Error initiating SIP call, Invalid URI");
                    return 0;
                }
            }

    }else{

          callbackObj->onDebugMessage("Error initiating SIP call, Invalid URI");
          return 0;

    }
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
void keepAlive()
{
    pjsua_acc_set_registration(acc_id, PJ_TRUE);
}
void resetEndpoint()
{
    pjsua_destroy();
}

//Register Deivce token with Plivo.
void registerToken(char *deviceToken)
{
    pjsua_acc_config acc_cfg;

    struct pjsip_generic_string_hdr CustomHeader;

    char buffer[2048];
    char buffer2[2048];

    pj_str_t contactparam;
    pj_str_t contactparam2;

    pj_bzero(buffer,sizeof(buffer));
    contactparam.ptr = buffer;

    pj_bzero(buffer2,sizeof(buffer2));
    contactparam2.ptr = buffer2;

    pj_str_t name = pj_str("AndroidToken");
    pj_str_t value = pj_str(deviceToken);
    pjsip_generic_string_hdr_init2(&CustomHeader, &name, &value);

    pjsua_acc_get_config(acc_id, app_pool,&acc_cfg);

    pj_list_push_back(&acc_cfg.reg_hdr_list, &CustomHeader);

    pj_strcpy2(&contactparam,";app_id=");
    pj_strcat (&contactparam,&value);

    pj_str_t value2 = pj_str("GCM");
    pj_strcpy2(&contactparam2,";app_type=");
    pj_strcat (&contactparam2,&value2);

    pj_strcat (&contactparam,&contactparam2);

    acc_cfg.contact_uri_params = contactparam;

    pj_status_t status = pjsua_acc_modify(acc_id, &acc_cfg);

    if (status != PJ_SUCCESS)
         fprintf(stderr, "Error in Register token funciton");

}

//pushMessage is the string forwarded by the GCM or FCM push notification service.
//Need to split the string to get key values
//PushMessage string will be in this format ("  label:"labelValue", index:"indexValue",  registrar:registrarValue"  ");
void relayVoipPushNotification(char *pushMessage)
{

    pj_str_t pjLabel; //label
    pj_str_t pjIndex; //index

    char *charRegistrar; //registrar

    //Static string to compare key value in Map
    //key[i] == stdLabel
    std::string stdLabel("label");

    //Static string to compare key value in Map
    //key[i] == stdIndex
    std::string stdIndex("index");

    //Static string to compare key value in Map
    //key[i] == stdRegistrar
    std::string stdRegistrar("registrar");

    //string vector
    //To store keys
    std::vector<std::string> key;

    //string vector
    //To store values
    vector<string> value;

    string str(pushMessage);

    //Split pushMessage string
    //Store Keys in Key Vector
    //Store Values in Value Vector
    vector<string> hdr_vec = split(str, ',');

    for (int i=0; i< hdr_vec.size();i++) {
        vector<string> each_vec = split(hdr_vec[i], ':');
        key.push_back(each_vec[0]);
        value.push_back(each_vec[1]);
    }

    //Loop the key vector and compare label, index, registrar values
    for (int i=0; i< key.size();i++) {


        if (key[i] == stdLabel) {

            char *charLabel = new char[value[i].length() + 1];
            strcpy(charLabel, value[i].c_str());
            pjLabel = pj_str(charLabel);

        }else if(key[i] == stdIndex){

            char *charIndex = new char[value[i].length() + 1];
            strcpy(charIndex, value[i].c_str());
            pjIndex = pj_str(charIndex);

        }else if(key[i] == stdRegistrar){

            charRegistrar = new char[value[i].length() + 1];
            strcpy(charRegistrar, value[i].c_str());
            //pjRegistrar = pj_str(charRegistrar);

        }else
        {

        }
    }


    pjsua_acc_config acc_cfg;

    struct pjsip_generic_string_hdr LabelHeader,IndexHeader;

    pj_str_t name1 = pj_str("X-Label");
    pj_str_t value1 = pjLabel;

    pjsip_generic_string_hdr_init2(&LabelHeader, &name1, &value1);

    pj_str_t name2 = pj_str("X-Index");
    pj_str_t value2 = pjIndex;

    pjsip_generic_string_hdr_init2(&IndexHeader, &name2, &value2);

    pjsua_acc_get_config(acc_id, app_pool,&acc_cfg);

    /* remove old X-Label and X-Index from the reg_hdr_list */
    // Iterate list nodes.
    struct pjsip_hdr *it,*tmpIt;

    it = acc_cfg.reg_hdr_list.next;
    while (it != &acc_cfg.reg_hdr_list) {
        if(pj_strcmp(&name1, &it->name)==0 || pj_strcmp(&name2, &it->name)==0){
            tmpIt = it->next;
            pj_list_erase(it);
            it = tmpIt;
        }else{
            it = it->next;
        }
    }

    pj_list_push_back(&acc_cfg.reg_hdr_list, &LabelHeader);
    pj_list_push_back(&acc_cfg.reg_hdr_list, &IndexHeader);

    std::string cReg = std::string(charRegistrar);
    std::string sipReg = "sip:"+cReg+";transport=tls";

    char *sipRegistrar = new char[sipReg.length() + 1];
    strcpy(sipRegistrar, sipReg.c_str());

    pj_str_t pjProxy = pj_str(sipRegistrar);

    acc_cfg.proxy_cnt = 0;

    pj_strdup_with_null(app_pool, &acc_cfg.proxy[acc_cfg.proxy_cnt++], &pjProxy);

    pj_status_t status = pjsua_acc_modify(acc_id, &acc_cfg);

    if (status != PJ_SUCCESS)
         fprintf(stderr, "Error in relayVoipPushNotification funciton");
}

#endif


 // Call canceled or timeout from the other side before answering
           //else if (call_info.state == PJSIP_INV_STATE_DISCONNECTED  && (call_info.last_status >= 486 && call_info.last_status <= 489)) {
           //       callbackObj->onDebugMessage("onCallDisconnected or timeout");
           //       callbackObj->onOutgoingCallRejected(call_id, pj_strbuf(&call_info.call_id));
           //}

 // Timeout
          //else if (call_info.state == PJSIP_INV_STATE_DISCONNECTED && call_info.last_status == 408) {
          //     callbackObj->onDebugMessage("onCallTimeout");
          //       callbackObj->onOutgoingCallInvalid(call_id, pj_strbuf(&call_info.call_id));
          //}
