package org.duckdns.altered.kellytv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
//import android.widget.Toast;

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

        //Toast.makeText(context, settings.getUrl(), Toast.LENGTH_LONG).show();

        context.startService(i);
    }
}
