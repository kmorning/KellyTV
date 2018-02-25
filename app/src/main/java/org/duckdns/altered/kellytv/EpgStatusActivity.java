package org.duckdns.altered.kellytv;

import android.app.Activity;
import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.util.Log;

/**
 * Created by kmorning on 2018-02-25.
 */

public class EpgStatusActivity extends Activity{
    private static final String TAG = EpgStatusActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        GuidedStepFragment.addAsRoot(this, new EpgStatusFragment(), android.R.id.content);
    }
}