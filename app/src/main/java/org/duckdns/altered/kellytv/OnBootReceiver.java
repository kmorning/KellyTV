package org.duckdns.altered.kellytv;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by kmorning on 2018-03-10.
 */

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        EpgStoredSettings settings = new EpgStoredSettings(context);
        // If auto update is enabled, set alarm schedule
        if (settings.getAutoUpdate()) {
            // Start delay
            long startDelayMillis = 5 * 60 * 1000;  // 5 minutes
            AlarmHelper.scheduleAlarm(context, startDelayMillis, settings.getIntervalMillis());
        }
    }
}
