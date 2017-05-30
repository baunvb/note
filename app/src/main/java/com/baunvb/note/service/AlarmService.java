package com.baunvb.note.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.baunvb.note.R;
import com.baunvb.note.fragments.CreateNoteFragment;

import java.util.Calendar;

public class AlarmService extends Service {
    private NotificationManager alarmNotificationManager;

    private int id;

    Uri alarmUri;
    Ringtone ringtone;

    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder();
    }

    public class ServiceBinder extends Binder {
        public AlarmService getService() {
            return AlarmService.this;
        }
    }

    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {

        switch (intent.getAction()) {
            case "start":
                CreateNoteFragment createNoteFragment = new CreateNoteFragment();
                id = createNoteFragment.getIdNote();
                sendNotification(getString(R.string.notification_alarm), this, intent);
                return Service.START_STICKY;
            case "stop":
                stopService();
                stopForeground(true);
                stopSelf();
                return Service.START_NOT_STICKY;
            default:
                return super.onStartCommand(intent, flags, startId);
        }

    }


    public void sendNotification(String msg, Context context, Intent intent) {
        alarmNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        intent.setAction("stop");
        PendingIntent stopPI = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context)
                .setAutoCancel(false)
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_alarm)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(R.drawable.ic_action_cancel, getString(R.string.cacel_label), stopPI)
                .setContentText(msg)
                .build();

        alarmNotificationManager.notify(id, notification);
        alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();
    }

    public void stopService() {
        alarmNotificationManager.cancel(id);
        ringtone.stop();
        ringtone = null;
    }

    public void setAlarmFire(int id, int day, int month, int year, int hour, int minute) {
        AlarmManager manager = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
        Intent launchIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, launchIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        manager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

    }

}