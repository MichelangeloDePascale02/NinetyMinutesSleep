package com.swdp31plus.ninetyminutessleep.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.ui.main.MainActivity;
import com.swdp31plus.ninetyminutessleep.utilities.ValuesUtilities;

import java.util.concurrent.TimeUnit;

public class PomodoroService extends Service {

    private final IBinder binder = new LocalBinder();

    private final String CHANNEL_ID = "TimerChannel";
    private final int ONGOING_NOTIFICATION_ID = 1;
    private final int FINISHED_NOTIFICATION_ID = 2;

    private CountDownTimer timer;
    private long remainingTime = 0;

    private NotificationManager notificationManager;
    private Ringtone ringtone;
    private Vibrator vibrator;
    private TimerUpdateListener timerUpdateListener;
    public interface TimerUpdateListener {
        void onTimerUpdate(long remainingTime);
    }
    public void setTimerUpdateListener(TimerUpdateListener listener) {
        this.timerUpdateListener = listener;
    }

    private void notifyTimerUpdate(long remainingTime) {
        if (timerUpdateListener != null) {
            timerUpdateListener.onTimerUpdate(remainingTime);
        }
    }

    public class LocalBinder extends Binder {
        public PomodoroService getService() {
            return PomodoroService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = getSystemService(NotificationManager.class);
        createNotificationChannel();
        startForeground(ONGOING_NOTIFICATION_ID, createOngoingNotification());
        Log.d("Pomodoro Service", "onCreate activated!");
    }

    public void startTimer(long duration) {
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished;
                timerUpdateListener.onTimerUpdate(remainingTime);
                updateOngoingNotification();
            }

            @Override
            public void onFinish() {
                stopTimer();
                playNotificationSound();
                updateFinishedNotification();
            }
        };

        timer.start();
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timerUpdateListener.onTimerUpdate((ValuesUtilities.PomodoroFlags.TOTAL_TIME) * 1000L * 60);
            deleteOngoingNotification();
        }

        stopForeground(true);
        stopSelf();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Timer Channel",
                    NotificationManager.IMPORTANCE_LOW
            );

            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createOngoingNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE
        );

        @SuppressLint("DefaultLocale") String mmss = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(remainingTime) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(remainingTime) % TimeUnit.MINUTES.toSeconds(1));

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Timer")
                .setContentText("Time left: " + mmss)
                .setSmallIcon(R.drawable.alarm_off_24)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    private Notification createFinishedNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Timer")
                .setContentText("Time's up!")
                .setSmallIcon(R.drawable.alarm_off_24)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
    }

    private void updateOngoingNotification() {
        Notification notification = createOngoingNotification();
        notificationManager.notify(ONGOING_NOTIFICATION_ID, notification);
        //Log.d("Pomodoro Service", "notification updated!");
    }

    private void updateFinishedNotification() {
        Notification notification = createFinishedNotification();
        notificationManager.notify(FINISHED_NOTIFICATION_ID, notification);
        Log.d("Pomodoro Service", "notification finished!");
    }

    private void deleteOngoingNotification() {
        notificationManager.cancel(ONGOING_NOTIFICATION_ID);
        Log.d("Pomodoro Service", "notification canceled!");
    }

    private void playNotificationSound() {
        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ringtone = RingtoneManager.getRingtone(this, notificationUri);
        ringtone.play();

        final long[] PATTERN = {0, 1000};
        vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(PATTERN, -1));
        } else {
            vibrator.vibrate(PATTERN, -1);
        }
        Log.d("Pomodoro Service", "notification played!");
    }

    public long getRemainingTime() {
        return remainingTime;
    }
}
