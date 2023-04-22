package com.swdp31plus.ninetyminutessleep;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class AlarmService extends Service {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private NotificationCompat.Builder notification;

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm1);
        mediaPlayer.setLooping(false); // TODO

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        notification = new NotificationCompat.Builder(this, "Cha_ID")
                .setContentText("Click on the notification to cancel the alarm.")
                .setSmallIcon(R.drawable.baseline_check_24);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, AlarmActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        String alarmTitle = "Alarm is ringing";

        mediaPlayer.start();

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 150, 800, 300, 1000};

        vibrator.vibrate(pattern, 0);

        startForeground(1, notification.setContentTitle(alarmTitle)
                .setContentIntent(pendingIntent)
                .build());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.stop();
        vibrator.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}