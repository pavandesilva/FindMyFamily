package com.finalapp.findmyfamily.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.finalapp.findmyfamily.R;

public class NotificationHelper extends ContextWrapper {
    private static final String EDMT_CHANNEL_ID = "com.finalapp.findmyfamily";
    private static final String EDMT_CHANNEL_NAME = "FindMyFamily";

    private NotificationManager manager;

    public NotificationHelper(Context base){
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel edmtChannel = new NotificationChannel(EDMT_CHANNEL_ID,
                    EDMT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            edmtChannel.enableLights(false);
            edmtChannel.enableVibration(true);
            edmtChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            getManager().createNotificationChannel(edmtChannel);
        }
    }

    public NotificationManager getManager() {
        if(manager == null)
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    public Notification.Builder getRealtimeTrachingNotification(String title, String content, Uri defaultSound){
            return new Notification.Builder(getApplicationContext(),EDMT_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSound(defaultSound)
                    .setAutoCancel(false);
        }
}
