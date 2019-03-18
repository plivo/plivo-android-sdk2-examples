package com.plivo.plivoincomingcall.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {
    private Context context;

    public DateUtils(Context context) {

        this.context = context;
    }

    public String formatDuration(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date );
    }

    public String prettyRelativeDate(long dateMillis) {
        return android.text.format.DateUtils.getRelativeDateTimeString(context, dateMillis,
                android.text.format.DateUtils.SECOND_IN_MILLIS, android.text.format.DateUtils.HOUR_IN_MILLIS, 0).toString();
    }
}
