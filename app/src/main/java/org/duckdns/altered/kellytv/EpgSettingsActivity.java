package org.duckdns.altered.kellytv;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.util.Log;

import java.util.List;

/**
 * Created by kmorning on 2018-02-18.
 */

public class EpgSettingsActivity extends Activity {
    private static final String TAG = EpgSettingsActivity.class.getSimpleName();

    /* Action ID definition */
    private static final int ACTION_URL = 0;
    private static final int ACTION_UPDATE_INTERVAL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            GuidedStepFragment.add(getFragmentManager(), new EpgSettingsFragment());
        }
    }

    private static void addEditableAction(List<GuidedAction> actions, long id, String title, String desc) {
        actions.add(new GuidedAction.Builder()
        .id(id)
        .title(title)
        .description(desc).descriptionEditable(true)
        .build());
    }

    public static class EpgSettingsFragment extends GuidedStepFragment {
        @NonNull
        @Override
        public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
            String title = "EPG Settings";
            String breadcrumb = "";
            String description = "";
            Drawable icon = getActivity().getDrawable(R.drawable.ic_main_icon);

            return new GuidanceStylist.Guidance(title, description, breadcrumb, icon);
        }

        @Override
        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            addEditableAction(actions, ACTION_URL, "EPG URL", "http://");
            addEditableAction(actions, ACTION_UPDATE_INTERVAL, "Update Interval", "1hr");
        }

        @Override
        public void onGuidedActionClicked(GuidedAction action) {
            if (ACTION_URL == action.getId()) {
                Log.d("editedText", action.getDescription().toString());
            } else if (ACTION_UPDATE_INTERVAL == getId()) {
                Log.d("editedText", action.getDescription().toString());
            }
        }
    }
}
