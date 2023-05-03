package com.swdp31plus.ninetyminutessleep;

import android.app.Notification;
import android.app.NotificationManager;
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

import com.swdp31plus.ninetyminutessleep.entities.Alarm;

import static android.provider.MediaStore.MediaColumns.TITLE;

public class AlarmService extends Service {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm1);
        mediaPlayer.setLooping(false); // TODO

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String alarmTitle = String.format("%s Alarm", intent.getStringExtra(TITLE));
        Alarm currentAlarm = (Alarm) intent.getSerializableExtra("Alarm");

        Intent notificationIntent = new Intent(this, AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, currentAlarm.getUniqueID(), notificationIntent, PendingIntent.FLAG_IMMUTABLE);



        notification = new NotificationCompat.Builder(this, "Cha_ID")
                .setContentTitle(alarmTitle)
                .setContentText("Click on the notification to cancel the alarm. " + currentAlarm.toString())
                .setSmallIcon(R.drawable.baseline_check_24)
                .setContentIntent(pendingIntent)
                .build();

        mediaPlayer.start();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 150, 800, 300, 800};

        vibrator.vibrate(pattern, 0);

        Intent cancelIntent = new Intent("CANCEL_OBJECT");
        intent.putExtra("uniqueID", currentAlarm.getUniqueID());
        sendBroadcast(cancelIntent);

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