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

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class AlarmService extends Service {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = MediaPlayer.create(this, R.raw.birds);
        mediaPlayer.setLooping(false);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        String alarmTitle = String.format("%s Alarm", intent.getStringExtra(getResources().getString(R.string.alarm)));

        Notification notification = new NotificationCompat.Builder(this, "Cha_ID")
                .setContentTitle(alarmTitle)
                .setContentText("Wake up!")
                .setSmallIcon(R.drawable.baseline_check_24)
                .setContentIntent(pendingIntent)
                .build();

        mediaPlayer.start();

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));

        startForeground(1, notification);

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