package org.duckdns.altered.kellytv;

import android.app.Application;
import android.content.Context;

/**
 * Created by kmorning on 2018-02-27.
 */

public class KellyTV extends Application{
    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }
}