/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.plivo.endpoint.backend;

public class plivo {
  public static int plivoStart() {
    return plivoJNI.plivoStart();
  }

  public static void plivoDestroy() {
    plivoJNI.plivoDestroy();
  }

  public static int plivoRestart() {
    return plivoJNI.plivoRestart();
  }

  public static int Call(String dest) {
    return plivoJNI.Call(dest);
  }

  public static int CallH(String dest, String headers) {
    return plivoJNI.CallH(dest, headers);
  }

  public static int Login(String username, String password) {
    return plivoJNI.Login(username, password);
  }

  public static int Logout() {
    return plivoJNI.Logout();
  }

  public static void keepAlive() {
    plivoJNI.keepAlive();
  }

  public static void resetEndpoint() {
    plivoJNI.resetEndpoint();
  }

  public static int Answer(int pjsuaCallId) {
    return plivoJNI.Answer(pjsuaCallId);
  }

  public static int Hangup(int pjsuaCallId) {
    return plivoJNI.Hangup(pjsuaCallId);
  }

  public static int Reject(int pjsuaCallId) {
    return plivoJNI.Reject(pjsuaCallId);
  }

  public static int SendDTMF(int pjsuaCallId, String digit) {
    return plivoJNI.SendDTMF(pjsuaCallId, digit);
  }

  public static int Mute(int pjsuaCallId) {
    return plivoJNI.Mute(pjsuaCallId);
  }

  public static int UnMute(int pjsuaCallId) {
    return plivoJNI.UnMute(pjsuaCallId);
  }

  public static void setCallbackObject(PlivoAppCallback callback) {
    plivoJNI.setCallbackObject(PlivoAppCallback.getCPtr(callback), callback);
  }

  public static void registerToken(String deviceToken) {
    plivoJNI.registerToken(deviceToken);
  }

  public static void relayVoipPushNotification(String pushMessage) {
    plivoJNI.relayVoipPushNotification(pushMessage);
  }

}
