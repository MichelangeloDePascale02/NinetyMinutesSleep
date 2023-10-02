package com.swdp31plus.ninetyminutessleep.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.swdp31plus.ninetyminutessleep.ui.main.NotificationActivity;
import com.swdp31plus.ninetyminutessleep.entities.NewAlarm;

import java.io.Serializable;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NewAlarm alarm = intent.getParcelableExtra("alarm");

        Log.e("Log in alarmreceiver", "alarmID: " + alarm.getId());

        if (alarm.getId() != -1) {

            Log.e("new alarm", alarm.toString());

            // Starting NotificationActivity
            Intent notificationIntent = new Intent(context, NotificationActivity.class);
            notificationIntent.putExtra("alarmID", alarm.getId());
            notificationIntent.putExtra("alarmTime", alarm.getTime());
            notificationIntent.putExtra("alarmObject", (Serializable) alarm);
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
        }
    }
}
