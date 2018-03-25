package org.duckdns.altered.kellytv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.util.Log;

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
    private static final int ACTION_DEFAULTS = 4;

    /* Auto update on/off options */
    private static final int AUTO_UPDATE_OPTION_SET_ID = 10;
    private static final String[] AUTO_UPDATE_OPTION_NAMES = {"on", "off"};

    /* Interval units options */
    private static final int INTERVAL_UNITS_OPTION_SET_ID = 11;

    /* Defaults options */
    private static final int DEFAULTS_OPTION_SET_ID = 12;
    private static final String[] DEFAULTS_OPTION_NAMES = {"Load Default Settings", "Cancel"};

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

        GuidedStepHelper.addActionWithSub(actions, context, ACTION_AUTO_UPDATE, "Auto Update", "");
        GuidedStepHelper.addEditableAction(actions, context, ACTION_URL, "EPG URL", "");
        GuidedStepHelper.addEditableAction(actions, context, ACTION_INTERVAL_VALUE, "Update Interval Value", "");
        GuidedStepHelper.addActionWithSub(actions, context, ACTION_INTERVAL_UNITS, "Update Interval Units", "");
        GuidedStepHelper.addActionWithSub(actions, context, ACTION_DEFAULTS, "Defaults", "");
    }

    @Override
    public void onResume() {
        super.onResume();

        // load settings
        mSettings = new EpgStoredSettings(getActivity());
        GuidedAction autoUpdateAction = findActionById(ACTION_AUTO_UPDATE);
        List<GuidedAction> autoUpdateSubActions = autoUpdateAction.getSubActions();
        boolean checked[] = new boolean[2];
        checked[0] = mSettings.getAutoUpdate();
        checked[1] = !mSettings.getAutoUpdate();
        for (int i = 0; i < AUTO_UPDATE_OPTION_NAMES.length; i++) {
            GuidedStepHelper.addCheckedAction(autoUpdateSubActions,
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
        intervalValueAction.setDescription(Long.toString(mSettings.getIntervalValue()));

        GuidedAction intervalUnitsAction = findActionById(ACTION_INTERVAL_UNITS);
        //intervalUnitsAction.setDescription(mSettings.getIntervalUnits());
        List<GuidedAction> unitsSubActions = intervalUnitsAction.getSubActions();
        EpgStoredSettings.IntervalUnits storedUnits =
                EpgStoredSettings.IntervalUnits.valueOf(mSettings.getIntervalUnits());
        for (int i = 0; i < EpgStoredSettings.IntervalUnits.values().length; i++) {
            EpgStoredSettings.IntervalUnits units = EpgStoredSettings.IntervalUnits.values()[i];
            GuidedStepHelper.addCheckedAction(unitsSubActions,
                    getActivity(),
                    units.name(),
                    INTERVAL_UNITS_OPTION_SET_ID,
                    units == storedUnits);
        }
        intervalUnitsAction.setDescription(storedUnits.name());

        GuidedAction defaultsAction = findActionById(ACTION_DEFAULTS);
        List<GuidedAction> defaultsSubActions = defaultsAction.getSubActions();
        for (int i = 0; i < DEFAULTS_OPTION_NAMES.length; i++) {
            GuidedStepHelper.addCheckedAction(defaultsSubActions,
                    getActivity(),
                    DEFAULTS_OPTION_NAMES[i],
                    DEFAULTS_OPTION_SET_ID,
                    false);
        }
    }
    /* Update alarm manager if auto enable is true
        If auto update or url changes, update immediately,
        if interval value or units change, calculate first
        update as interval - (currentTime - lastUpdateTime),
        immediately if less than zero.
     */
    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        if (ACTION_URL == action.getId()) {
            String newUrl = action.getDescription().toString();
            if (!newUrl.equals(mSettings.getUrl())) {
                mSettings.setUrl(action.getDescription().toString());
                // Set alarm and update now if url changed and auto update is on
                if (mSettings.getAutoUpdate()) {
                    AlarmHelper.scheduleAlarm(getActivity(), 0,
                            mSettings.getIntervalMillis());
                }
            }
        } else if (ACTION_INTERVAL_VALUE == action.getId()) {
            //Log.d("editedText", action.getDescription().toString());
            try {
                int newVal = Integer.parseInt(action.getDescription().toString());
                // Set alarm and update on the newly calculated next interval
                if (newVal != mSettings.getIntervalValue()) {
                    mSettings.setIntervalValue(newVal);
                    // Set alarm and update now if interval value changed and auto update is on
                    if (mSettings.getAutoUpdate()) {
                        long interval = mSettings.getIntervalMillis();
                        AlarmHelper.scheduleAlarm(getActivity(), getNextUpdateMillis(interval),
                                interval);
                    }
                }
                // If auto update is enabled, set the new alarm interval
            } catch (NumberFormatException e) {
                action.setDescription(Long.toString(mSettings.getIntervalValue()));
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
            // Turn alarm on or offf
            if (mSettings.getAutoUpdate()) {
                AlarmHelper.scheduleAlarm(getActivity(), 0,
                        mSettings.getIntervalMillis());
            } else {
                AlarmHelper.cancelAlarm(getActivity());
            }
            return true;
        } else if (action.getCheckSetId() == INTERVAL_UNITS_OPTION_SET_ID && action.isChecked()) {
            String selection = action.getTitle().toString();
            findActionById(ACTION_INTERVAL_UNITS).setDescription(selection);
            notifyActionChanged(findActionPositionById(ACTION_INTERVAL_UNITS));
            mSettings.setIntervalUnits(selection);
            // Set alarm and update now if interval value changed and auto update is on
            if (mSettings.getAutoUpdate()) {
                long interval = mSettings.getIntervalMillis();
                AlarmHelper.scheduleAlarm(getActivity(), getNextUpdateMillis(interval),
                        interval);
            }
            return true;
        } else if (action.getCheckSetId() == DEFAULTS_OPTION_SET_ID && action.isChecked()) {
            if (action.getTitle() == DEFAULTS_OPTION_NAMES[0]) {
                mSettings.restoreDefaults();
                updateActions();
            }
            action.setChecked(false);
            notifyActionChanged(findActionPositionById(ACTION_DEFAULTS));
            return true;
        } else {
            return false;
        }
    }

    private void updateActions() {
        // load settings
        //mSettings = new EpgStoredSettings(getActivity());

        GuidedAction autoUpdateAction = findActionById(ACTION_AUTO_UPDATE);
        List<GuidedAction> autoUpdateSubActions = autoUpdateAction.getSubActions();
        autoUpdateSubActions.get(0).setChecked(mSettings.getAutoUpdate());
        autoUpdateSubActions.get(1).setChecked(!mSettings.getAutoUpdate());
        if (mSettings.getAutoUpdate()) {
            autoUpdateAction.setDescription(AUTO_UPDATE_OPTION_NAMES[0]);
        } else {
            autoUpdateAction.setDescription(AUTO_UPDATE_OPTION_NAMES[1]);
        }
        notifyActionChanged(findActionPositionById(ACTION_AUTO_UPDATE));

        GuidedAction urlAction = findActionById(ACTION_URL);
        urlAction.setDescription(mSettings.getUrl());
        notifyActionChanged(findActionPositionById(ACTION_URL));

        GuidedAction intervalValueAction = findActionById(ACTION_INTERVAL_VALUE);
        intervalValueAction.setDescription(Integer.toString(mSettings.getIntervalValue()));
        notifyActionChanged(findActionPositionById(ACTION_INTERVAL_VALUE));

        GuidedAction intervalUnitsAction = findActionById(ACTION_INTERVAL_UNITS);
        List<GuidedAction> intervalUnitsSubActions = intervalUnitsAction.getSubActions();
        for (GuidedAction action: intervalUnitsSubActions
             ) {
            if (action.getTitle().toString().equals(mSettings.getIntervalUnits())) {
                action.setChecked(true);
                intervalUnitsAction.setDescription(action.getTitle().toString());
            } else {
                action.setChecked(false);
            }
        }
        notifyActionChanged(findActionPositionById(ACTION_INTERVAL_UNITS));
    }

    private long getNextUpdateMillis(long interval) {
        long now = System.currentTimeMillis();
        long nextUpdate = interval - now + mSettings.getLastUpdateTime().getTime();
        if (nextUpdate < 0) {
            return 0;
        } else {
            return nextUpdate;
        }
    }
}
