package com.swdp31plus.ninetyminutessleep;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ebanx.swipebtn.OnStateChangeListener;
import com.swdp31plus.ninetyminutessleep.databinding.ActivityMainBinding;
import com.swdp31plus.ninetyminutessleep.databinding.ActivityNotificationBinding;

import java.text.SimpleDateFormat;
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

        SimpleDateFormat s = new SimpleDateFormat("HH:mm");

        binding.notificationText.setText("" + s.format(alarmTime.getTime()) + " - " + alarmID);

        // Aggiorna lo stato della sveglia nel modello di dati (se necessario)

        playAlarmSound();
    }

    private void playAlarmSound() {

        alert = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);
        if(alert == null) {
            alert = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION);
            if(alert == null) {
                alert = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);
            }
        }

        ringtone = RingtoneManager.getRingtone(this, alert);
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

