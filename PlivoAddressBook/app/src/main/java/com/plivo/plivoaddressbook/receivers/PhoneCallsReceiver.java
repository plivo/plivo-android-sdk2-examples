package com.plivo.plivoaddressbook.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.plivo.plivoaddressbook.model.internal.TelephonyCall;

import org.greenrobot.eventbus.EventBus;

import static android.telephony.TelephonyManager.*;

/**
 * Receives the cellular call states
 */
public class PhoneCallsReceiver extends BroadcastReceiver {
    private static final String TAG = PhoneCallsReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive " + intent);
        if (intent.getExtras() == null) return;

        String telephoneCallState = intent.getExtras().getString(EXTRA_STATE);
        Log.d(TAG, "telephoneCallState " + telephoneCallState);
        if (telephoneCallState == null) return;

        String phone_number = intent.getExtras().getString(EXTRA_INCOMING_NUMBER);
        Log.d(TAG, "phone_number " + phone_number);
        int state = telephoneCallState.equalsIgnoreCase(EXTRA_STATE_OFFHOOK) ? CALL_STATE_OFFHOOK :
                telephoneCallState.equalsIgnoreCase(EXTRA_STATE_RINGING) ? CALL_STATE_RINGING :
                CALL_STATE_IDLE;
        Log.d(TAG, "state " + state);
        EventBus.getDefault().post(new TelephonyCall(state, phone_number));
    }
}
