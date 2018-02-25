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
    private static final int AUTO_UPDATE_OPTION_SET_ID = 10;
    private static final String[] AUTO_UPDATE_OPTION_NAMES = {"on", "off"};

    /* Interval units options */
    private static final int INTERVAL_UNITS_OPTION_SET_ID = 11;

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
        addEditableAction(actions, context, ACTION_INTERVAL_VALUE, "Update Interval Value", "");
        //addEditableAction(actions, context, ACTION_INTERVAL_UNITS, "Update Units", "");
        addActionWithSub(actions, context, ACTION_INTERVAL_UNITS, "Update Interval Units", "");
    }

    @Override
    public void onResume() {
        super.onResume();
        updateActions();
    }

    public void updateActions() {
        // load settings
        mSettings = new EpgStoredSettings(getActivity());
        GuidedAction autoUpdateAction = findActionById(ACTION_AUTO_UPDATE);
        List<GuidedAction> autoUpdateSubActions = autoUpdateAction.getSubActions();
        boolean checked[] = new boolean[2];
        checked[0] = mSettings.getAutoUpdate();
        checked[1] = !mSettings.getAutoUpdate();
        for (int i = 0; i < AUTO_UPDATE_OPTION_NAMES.length; i++) {
            addCheckedAction(autoUpdateSubActions,
                    getActivity(),
                    AUTO_UPDATE_OPTION_NAMES[i],
                    AUTO_UPDATE_OPTION_SET_ID,
                    checked[i]);
        }
        if (mSettings.getAutoUpdate()) {
            autoUpdateAction.setDescription(AUTO_UPDATE_OPTION_NAMES[0]);
        } else {
            autoUpdateAction.setDescription(AUTO_UPDATE_OPTION_NAMES[1]);
        }
        
        GuidedAction urlAction = findActionById(ACTION_URL);
        urlAction.setDescription(mSettings.getUrl());
        
        GuidedAction intervalValueAction = findActionById(ACTION_INTERVAL_VALUE);
        intervalValueAction.setDescription(Integer.toString(mSettings.getIntervalValue()));

        GuidedAction intervalUnitsAction = findActionById(ACTION_INTERVAL_UNITS);
        //intervalUnitsAction.setDescription(mSettings.getIntervalUnits());
        List<GuidedAction> unitsSubActions = intervalUnitsAction.getSubActions();
        EpgStoredSettings.IntervalUnits storedUnits =
                EpgStoredSettings.IntervalUnits.valueOf(mSettings.getIntervalUnits());
        for (int i = 0; i < EpgStoredSettings.IntervalUnits.values().length; i++) {
            EpgStoredSettings.IntervalUnits units = EpgStoredSettings.IntervalUnits.values()[i];
            addCheckedAction(unitsSubActions,
                    getActivity(),
                    units.name(),
                    INTERVAL_UNITS_OPTION_SET_ID,
                    units == storedUnits);
        }
        intervalUnitsAction.setDescription(storedUnits.name());
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
        }
    }

    @Override
    public boolean onSubGuidedActionClicked(GuidedAction action) {
        if (action.getCheckSetId() == AUTO_UPDATE_OPTION_SET_ID && action.isChecked()) {
            String selection = action.getTitle().toString();
            findActionById(ACTION_AUTO_UPDATE).setDescription(selection);
            notifyActionChanged(findActionPositionById(ACTION_AUTO_UPDATE));
            int i = Arrays.asList(AUTO_UPDATE_OPTION_NAMES).indexOf(selection);
            mSettings.setAutoUpdate(i == 0);
            return true;
        } else if (action.getCheckSetId() == INTERVAL_UNITS_OPTION_SET_ID && action.isChecked()) {
            String selection = action.getTitle().toString();
            findActionById(ACTION_INTERVAL_UNITS).setDescription(selection);
            notifyActionChanged(findActionPositionById(ACTION_INTERVAL_UNITS));
            mSettings.setIntervalUnits(selection);
            return true;
        } else {
            return false;
        }
    }

    //public void updateActions() {}

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
                                         String title, int id, boolean checked) {
        GuidedAction guidedAction = new GuidedAction.Builder(context)
                .title(title)
                .checkSetId(id)
                .build();
        guidedAction.setChecked(checked);
        actions.add(guidedAction);

    }
}
