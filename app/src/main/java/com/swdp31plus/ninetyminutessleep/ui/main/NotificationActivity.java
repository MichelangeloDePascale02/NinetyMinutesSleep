package com.swdp31plus.ninetyminutessleep.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ebanx.swipebtn.OnStateChangeListener;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.databinding.ActivityMainBinding;
import com.swdp31plus.ninetyminutessleep.databinding.ActivityNotificationBinding;
import com.swdp31plus.ninetyminutessleep.entities.NewAlarm;
import com.swdp31plus.ninetyminutessleep.utilities.StorageUtilities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotificationActivity extends AppCompatActivity {
    private ActivityNotificationBinding binding;
    //private int alarmID;
    private Uri alert;
    private Ringtone ringtone;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }

        // Recupera l'ID della sveglia dalla notifica
        int alarmID = getIntent().getIntExtra("alarmID", -1);
        Date alarmTime = (Date) getIntent().getSerializableExtra("alarmTime");

        Log.e("Log in notificationactivity", "alarmID: " + alarmID);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        binding.notificationAlarmText.setText(sdf.format(alarmTime.getTime()));

        ArrayList<NewAlarm> readFromFile = (ArrayList<NewAlarm>) StorageUtilities.loadAlarms("currentAlarm.obj", getApplicationContext());
        if (readFromFile != null) {
            for (NewAlarm alarm : readFromFile) {
                Log.e("NotificationActivity","alarm# : " + alarm.toString());
            }
            if (readFromFile.remove(getIntent().getSerializableExtra("alarmObject"))) {
                StorageUtilities.saveAlarm((Serializable) readFromFile,"currentAlarm.obj", getApplicationContext());
            }
        }

        playAlarmSound();
    }

    private void playAlarmSound() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PowerManager powerManager = (PowerManager) this.getSystemService(POWER_SERVICE);
            if (!powerManager.isInteractive()){ // if screen is not already on, turn it on (get wake_lock)
                @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE |PowerManager.SCREEN_BRIGHT_WAKE_LOCK ,"id:wakeupscreen");
                wl.acquire(10*60*10L);
            }
        }

        alert = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);
        if(alert == null) {
            alert = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION);
            if(alert == null) {
                alert = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);
            }
        }

        ringtone = RingtoneManager.getRingtone(this, alert);
        ringtone.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build());
        ringtone.play();

        final long[] PATTERN = {0, 1000};
        vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createWaveform(PATTERN, 0));

        binding.notificationSwipebutton.setOnStateChangeListener(active -> {
            ringtone.stop();
            vibrator.cancel();
            finish();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, getString(R.string.dismiss_to_quit), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onUserLeaveHint() {
        ringtone.stop();
        vibrator.cancel();
        finish();
    }
}

