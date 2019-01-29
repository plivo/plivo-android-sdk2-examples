package com.plivo.plivoincomingcall.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.plivo.plivoincomingcall.R;
import com.plivo.plivoincomingcall.screens.login.LoginActivity;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class NotificationUtils {
    private static final String CHANNEL_ID = String.valueOf(R.id.notification_channel_id);
    private static final int PENDING_INTENT_REQUEST_CODE = 1;

    private Context context;

    public void createNotification(Context serviceContext) {
        if (serviceContext instanceof Service) {
            this.context = serviceContext;
            ((Service) context).startForeground(R.id.notification_ongoing_id, buildNotification());
        }
    }

    private Notification buildNotification() {
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(LoginActivity.class);
        taskStackBuilder.addNextIntent(new Intent(context, LoginActivity.class));


        PendingIntent pendingIntentAppLaunch = taskStackBuilder.getPendingIntent(PENDING_INTENT_REQUEST_CODE,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(context, createChannel())
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText("")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntentAppLaunch)
                .setStyle(new NotificationCompat.BigTextStyle())
                .build();
    }

    @TargetApi(Build.VERSION_CODES.O)
    @NonNull
    private String createChannel() {

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel =
                new NotificationChannel(CHANNEL_ID,
                        context.getString(R.string.notification_channel_name),
                        NotificationManager.IMPORTANCE_HIGH);

        notificationManager.createNotificationChannel(channel);

        return channel.getId();
    }
}
