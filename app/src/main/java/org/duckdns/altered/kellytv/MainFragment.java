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

        setupUIElements();
    }

    private void setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(R.drawable.videos_by_google_banner));
        setTitle("Kelly TV"); // Badge, when set, takes precedent over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }
}
