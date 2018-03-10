package org.duckdns.altered.kellytv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by kmorning on 2018-03-10.
 */

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // do nothing for now
        //WakefulIntentService.acquireStaticLock(context); //acquire a partial WakeLock
        //context.startService(new Intent(context, EPGUpdateService.class)); //start EPGUpdateService
    }
}
