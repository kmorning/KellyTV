package org.duckdns.altered.kellytv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by kmorning on 2018-03-11.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "org.duckdns.altered.kellytv.alarm";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");

        EpgStoredSettings settings = new EpgStoredSettings(context);
        Intent i = new Intent(context, EPGUpdateService.class);
        i.setData(Uri.parse(settings.getUrl()));

        Toast.makeText(context, "Updating EPG", Toast.LENGTH_LONG).show();

        // Setup dummy broadcast receiver, since settings fragment registers a receiver,
        // but may not have run yet, which in turn causes epgupdateservice to crash (probably
        // due to null pointer when no receiver is registered with LocalBroadcastManager).
        IntentFilter filter = new IntentFilter(Constants.BROADCAST_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, filter);

        context.startService(i);
    }

    // Define the callback for broadcast data received
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };
}
