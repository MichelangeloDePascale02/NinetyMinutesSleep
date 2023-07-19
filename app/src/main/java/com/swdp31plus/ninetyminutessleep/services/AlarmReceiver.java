package com.swdp31plus.ninetyminutessleep.services;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.swdp31plus.ninetyminutessleep.NotificationActivity;
import com.swdp31plus.ninetyminutessleep.R;
import com.swdp31plus.ninetyminutessleep.entities.NewAlarm;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Qui puoi gestire l'evento di allarme e avviare l'Activity di notifica

        int alarmID = intent.getIntExtra("alarmID", -1);
        NewAlarm alarm = intent.getParcelableExtra("alarm");

        Log.e("Log in alarmreceiver", "alarmID: " + alarm.getId());

        if (alarm.getId() != -1) {
            // Recupera la sveglia dal modello di dati utilizzando l'ID
            // NewAlarm alarm = getAlarmById(alarmID);

            Log.e("new alarm", alarm.toString());

            // Avvia l'Activity di notifica
            Intent notificationIntent = new Intent(context, NotificationActivity.class);
            notificationIntent.putExtra("alarmID", alarm.getId());
            notificationIntent.putExtra("alarmTime", alarm.getTime());
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(notificationIntent);

            /*PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "AlarmChannel")
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.notification_text))
                    .setSmallIcon(R.drawable.baseline_alarm_add_24)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    if (!notificationManager.areNotificationsEnabled()) {
                        Intent intent2 = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        intent2.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                        context.startActivity(intent);
                    }
            }
            notificationManager.notify(alarmID, builder.build());*/

            // Spegni la sveglia se è a ripetizione singola
            if (!alarm.isActive()) {
                alarm.setActive(false);
                // Aggiorna la sveglia nel modello di dati
                updateAlarm(alarm);
            }
        }
    }

    private NewAlarm getAlarmById(int alarmID) {
        // Recupera la sveglia dal tuo modello di dati utilizzando l'ID
        // Esempio: return SvegliaRepository.getSvegliaById(svegliaId);
        return null;
    }

    private void updateAlarm(NewAlarm alarm) {
        // Aggiorna la sveglia nel tuo modello di dati
        // Esempio: SvegliaRepository.aggiornaSveglia(sveglia);
    }
}
