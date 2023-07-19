package com.swdp31plus.ninetyminutessleep.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.swdp31plus.ninetyminutessleep.NotificationActivity;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.entities.Alarm;
import com.swdp31plus.ninetyminutessleep.entities.NewAlarm;
import com.swdp31plus.ninetyminutessleep.utilities.StorageUtilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmService extends Service {
    private AlarmManager alarmManager;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "String")
                .setContentTitle("Title")
                .setContentText("Content")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Qui puoi gestire l'attivazione delle sveglie e programmare gli allarmi
        // Recupera la lista delle sveglie dal modello di dati
        /*List<NewAlarm> sveglie = getListaSveglie();

        for (NewAlarm sveglia : sveglie) {
            if (sveglia.isActive()) {
                programmareAllarme(sveglia);
            }
        }*/

        NewAlarm newAlarm = intent.getParcelableExtra("alarm");

        Log.e("Log in alarmservice", newAlarm.toString());

        programmareAllarme(newAlarm);
        return START_STICKY;
    }

    private void programmareAllarme(NewAlarm alarm) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("alarm", alarm);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                alarm.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Imposta l'orario per l'allarme
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(alarm.getTime());

        Log.e("Log in alarmservice", "" + alarm.isRecurring());

        // Verifica se la sveglia è a ripetizione
        if (alarm.isRecurring()) {
            // Imposta l'allarme per i giorni di ripetizione specificati
            int[] giorniRipetizione = alarm.getRepeatingDays();
            for (int giorno : giorniRipetizione) {
                calendar.set(Calendar.DAY_OF_WEEK, giorno);
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY * 7,
                        pendingIntent
                );
            }
        } else {
            // Imposta l'allarme per un singolo giorno e orario
            Log.e("Log in alarmservice", "Millis correnti: " + System.currentTimeMillis());
            Log.e("Log in alarmservice", "Millis alarm:    " + calendar.getTimeInMillis());
            Log.e("Log in alarmservice", "Diff millis:     " + (calendar.getTimeInMillis() - System.currentTimeMillis()));

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private List<NewAlarm> getListaSveglie() {
        List<NewAlarm> alarms;
        if (StorageUtilities.loadAlarms("savedAlarms.obj", getApplicationContext()) == null) {
            alarms = new ArrayList<>();
        } else {
            alarms = (ArrayList<NewAlarm>) StorageUtilities.loadAlarms("savedAlarms.obj", getApplicationContext());
        }
        return alarms;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = "NinetyMinutesSleep Alarm Channel";
        String description = "This is a description";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("AlarmChannel", name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}
