package com.baunvb.note.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.baunvb.note.R;
import com.baunvb.note.activity.fragments.CreateNoteFragment;
import com.baunvb.note.activity.fragments.BaseFragment;
import com.baunvb.note.utils.Constant;

public class AlarmService extends Service {
    private NotificationManager notificationManager;

    private int id;

    Uri alarmUri;
    Ringtone ringtone;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() == Constant.ACTION_START_SERVICE) {
            BaseFragment createNoteFragment = new CreateNoteFragment();
            id = createNoteFragment.getIdNote();
            sendNotification(getString(R.string.notification_alarm), this, intent);
            return Service.START_STICKY;
        }
        if (intent.getAction() == Constant.ACTION_STOP_SERVICE){
            stopService();
            stopForeground(true);
            stopSelf();
            return Service.START_NOT_STICKY;
        }
        return START_NOT_STICKY;
    }

    public void sendNotification(String msg, Context context, Intent intent) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        intent.setAction(Constant.ACTION_STOP_SERVICE);
        PendingIntent stopPI = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context)
                .setAutoCancel(false)
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_action_alarms_select)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(R.drawable.ic_action_cancel, getString(R.string.cacel_label), stopPI)
                .setContentText(msg)
                .build();

        notificationManager.notify(id, notification);
        alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        if (ringtone == null){
            ringtone = RingtoneManager.getRingtone(context, alarmUri);
        }
        ringtone.play();
    }

    public void stopService() {
        notificationManager.cancelAll();
        ringtone.stop();
        ringtone = null;
    }

}