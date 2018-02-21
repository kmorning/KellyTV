package org.duckdns.altered.kellytv;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kmorning on 2018-02-18.
 */

public class EpgSettingsActivity extends Activity {
    private static final String TAG = EpgSettingsActivity.class.getSimpleName();

    /* Action ID definition */
    private static final int ACTION_URL = 0;
    private static final int ACTION_AUTO_UPDATE = 1;
    private static final int ACTION_UPDATE_INT_UNITS = 2;
    private static final int ACTION_UPDATE_INT_VALUE = 3;

    /* Auto update on/off options */
    private static final int UPDATE_OPTION_CHECK_SET_ID = 10;
    private static final String[] UPDATE_OPTION_NAMES = {"on", "off"};
    //private static final boolean[] UPDATE_OPTION_CHECKED = {true, false};
    protected boolean bAutoUpdate;
    //protected static ArrayList<String> sOnOffStates= new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            GuidedStepFragment.add(getFragmentManager(), new EpgSettingsFragment());
        }
    }

    private void loadSettings() {
        // TODO: Load saved settings here
        // For now just set to auto update on for now and url to something
        bAutoUpdate = true;

    }

    private static void addActionWithSub(List<GuidedAction> actions, Context context, long id,
                                         String title, String desc) {
        List<GuidedAction> subActions = new ArrayList();
        actions.add(new GuidedAction.Builder(context)
        .title(title)
        .id(id)
        .description(desc)
        .subActions(subActions)
        .build());
    }

    private static void addEditableAction(List<GuidedAction> actions, Context context, long id,
                                          String title, String desc) {
        actions.add(new GuidedAction.Builder(context)
        .id(id)
        .title(title)
        .description(desc).descriptionEditable(true)
        .build());
    }

    private static void addCheckedAction(List<GuidedAction> actions, Context context,
                                         String title, boolean checked) {
        GuidedAction guidedAction = new GuidedAction.Builder(context)
                .title(title)
                .checkSetId(UPDATE_OPTION_CHECK_SET_ID)
                .build();
        guidedAction.setChecked(checked);
        actions.add(guidedAction);

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
            Context context = getActivity();

            addActionWithSub(actions, context, ACTION_AUTO_UPDATE, "Auto Update", "");
            List<GuidedAction> subActions = findButtonActionById(ACTION_AUTO_UPDATE).getSubActions();
            for (int i = 0; i < UPDATE_OPTION_NAMES.length; i++) {
                /*addCheckedAction(subActions,
                        context,
                        UPDATE_OPTION_NAMES[i],
                        );*/
            }
            addEditableAction(actions, context, ACTION_URL, "EPG URL", "http://");
            addEditableAction(actions, context, ACTION_UPDATE_INT_VALUE, "Update Interval", "5");
            //addCheckedAction(actions, context, "test", true);
        }

        @Override
        public void onGuidedActionClicked(GuidedAction action) {
            if (ACTION_URL == action.getId()) {
                Log.d("editedText", action.getDescription().toString());
            } else if (ACTION_UPDATE_INT_VALUE == getId()) {
                Log.d("editedText", action.getDescription().toString());
            }
        }
    }
}
