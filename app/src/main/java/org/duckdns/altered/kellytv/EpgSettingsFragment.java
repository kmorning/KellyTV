package org.duckdns.altered.kellytv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kmorning on 2018-02-20.
 */

public class EpgSettingsFragment extends GuidedStepFragment {
    /* Action ID definition */
    private static final int ACTION_URL = 0;
    private static final int ACTION_AUTO_UPDATE = 1;
    private static final int ACTION_INTERVAL_VALUE = 2;
    private static final int ACTION_INTERVAL_UNITS = 3;

    /* Auto update on/off options */
    private static final int UPDATE_OPTION_CHECK_SET_ID = 10;
    private static final String[] UPDATE_OPTION_NAMES = {"on", "off"};
    //private static final boolean[] UPDATE_OPTION_CHECKED = {true, false};
    //private boolean[] checked = new boolean[2];

    private EpgStoredSettings mSettings;

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
        addEditableAction(actions, context, ACTION_URL, "EPG URL", "");
        addEditableAction(actions, context, ACTION_INTERVAL_VALUE, "Update Value", "");
        addEditableAction(actions, context, ACTION_INTERVAL_UNITS, "Update Units", "");
    }

    @Override
    public void onResume() {
        super.onResume();
        // load settings
        mSettings = new EpgStoredSettings(getActivity());
        GuidedAction autoUpdateAction = findActionById(ACTION_AUTO_UPDATE);
        List<GuidedAction> subActions = autoUpdateAction.getSubActions();
        boolean checked[] = new boolean[2];
        checked[0] = mSettings.getAutoUpdate();
        checked[1] = !mSettings.getAutoUpdate();
        for (int i = 0; i < UPDATE_OPTION_NAMES.length; i++) {
            addCheckedAction(subActions, getActivity(), UPDATE_OPTION_NAMES[i], checked[i]);
        }
        if (mSettings.getAutoUpdate()) {
            autoUpdateAction.setDescription(UPDATE_OPTION_NAMES[0]);
        } else {
            autoUpdateAction.setDescription(UPDATE_OPTION_NAMES[1]);
        }
        
        GuidedAction urlAction = findActionById(ACTION_URL);
        urlAction.setDescription(mSettings.getUrl());
        
        GuidedAction intervalValueAction = findActionById(ACTION_INTERVAL_VALUE);
        intervalValueAction.setDescription(Integer.toString(mSettings.getIntervalValue()));

        GuidedAction intervalUnitsAction = findActionById(ACTION_INTERVAL_UNITS);
        intervalUnitsAction.setDescription(mSettings.getIntervalUnits());
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        if (ACTION_URL == action.getId()) {
            // TODO: validate input
            //Log.d("editedText", action.getDescription().toString());
            mSettings.setUrl(action.getDescription().toString());
        } else if (ACTION_INTERVAL_VALUE == action.getId()) {
            //Log.d("editedText", action.getDescription().toString());
            try {
                int value = Integer.parseInt(action.getDescription().toString());
                mSettings.setIntervalValue(value);
            } catch (NumberFormatException e) {
                action.setDescription(Integer.toString(mSettings.getIntervalValue()));
            }
        } else if (ACTION_INTERVAL_UNITS == action.getId()) {
            // TODO: validate input
            mSettings.setIntervalUnits(action.getDescription().toString());
        }
    }

    @Override
    public boolean onSubGuidedActionClicked(GuidedAction action) {
        if (action.isChecked()) {
            String selection = action.getTitle().toString();
            findActionById(ACTION_AUTO_UPDATE).setDescription(selection);
            notifyActionChanged(findActionPositionById(ACTION_AUTO_UPDATE));
            int i = Arrays.asList(UPDATE_OPTION_NAMES).indexOf(selection);
            mSettings.setAutoUpdate(i == 0);
        }
        return true;
    }

    /*
    private void loadSettings() {

    }
    */

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
}
