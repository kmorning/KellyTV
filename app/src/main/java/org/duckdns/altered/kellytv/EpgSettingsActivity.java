package org.duckdns.altered.kellytv;

import android.app.Activity;
import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.util.Log;



/**
 * Created by kmorning on 2018-02-18.
 */

public class EpgSettingsActivity extends Activity {
    private static final String TAG = EpgSettingsActivity.class.getSimpleName();


    //protected static ArrayList<String> sOnOffStates= new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            //GuidedStepFragment.add(getFragmentManager(), new EpgSettingsFragment());
            GuidedStepFragment.addAsRoot(this, new EpgSettingsFragment(), android.R.id.content);
        }
    }
}
