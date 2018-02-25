package org.duckdns.altered.kellytv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;

import java.util.List;

/**
 * Created by kmorning on 2018-02-25.
 */

public class EpgStatusFragment extends GuidedStepFragment {
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        String title = "EPG Status";
        String breadcrumb = "";
        String description = "";
        Drawable icon = getActivity().getDrawable(R.drawable.ic_main_icon);

        return new GuidanceStylist.Guidance(title, description, breadcrumb, icon);
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        Context context = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {

    }
}
