package org.duckdns.altered.kellytv;

import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.util.Log;

/**
 * Created by kmorning on 2018-02-17.
 */

public class MainFragment extends BrowseFragment {
    private static final String TAG = MainFragment.class.getSimpleName();

    @Override
    public void onActivityCreated(Bundle savedInsanceState) {
        Log.i(TAG, "onActivityCreate");
        super.onActivityCreated(savedInsanceState);
    }
}
