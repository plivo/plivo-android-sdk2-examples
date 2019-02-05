package com.plivo.plivoaddressbook.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.plivo.plivoaddressbook.receivers.StarterServiceReceiver;

import java.util.concurrent.TimeUnit;

import static com.plivo.plivoaddressbook.utils.Constants.ACTION_ALARM_RECEIVED;
import static com.plivo.plivoaddressbook.utils.Constants.ALARM_INTENT_REQUEST_CODE;

public class AlarmUtils {

    private Context context;

    public AlarmUtils(Context context) {
        this.context = context;
    }

    public void setRepeatingAlarm() {
        alarmManager()
                .setRepeating(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis(),
                        TimeUnit.SECONDS.toMillis(PreferencesUtils.LOGIN_TIMEOUT),
                        getAlarmIntent());
    }

    public void cancelRepeatingAlarm() {
        alarmManager()
                .cancel(getAlarmIntent());
    }

    private AlarmManager alarmManager() {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private PendingIntent getAlarmIntent() {
        Intent alarmBroadcastIntent = new Intent(context, StarterServiceReceiver.class)
                .setAction(ACTION_ALARM_RECEIVED);
        return PendingIntent.getBroadcast(context, ALARM_INTENT_REQUEST_CODE, alarmBroadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
