package org.duckdns.altered.kellytv;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
//import android.widget.Toast;

/**
 * Created by kmorning on 2018-03-11.
 */

public class AlarmHelper {
    public static void scheduleAlarm(Context context, long startDelayMillis, long intervalMillis) {
        // Construct an intent that will execute the AlarmReceiver
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, AlarmReceiver.REQUEST_CODE,
                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every every half hour from this point onwards
        long firstMillis = System.currentTimeMillis() + startDelayMillis; // alarm is set after start Delay
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                intervalMillis, pIntent);
        //Toast.makeText(context, "Auto update set for " + firstMillis + ", every " + intervalMillis, Toast.LENGTH_SHORT).show();
    }

    public static void cancelAlarm(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
        //Toast.makeText(context, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }
}
