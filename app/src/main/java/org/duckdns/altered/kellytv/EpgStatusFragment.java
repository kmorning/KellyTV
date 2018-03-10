package org.duckdns.altered.kellytv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Date;
import java.util.List;

/**
 * Created by kmorning on 2018-02-25.
 */

public class EpgStatusFragment extends GuidedStepFragment {
    private Intent mServiceIntent;

    /* Action ID definition */
    private static final int ACTION_EPG_UPDATE_SERVICE = 0;
    private static final int ACTION_LAST_UPDATE_TIME = 1;
    private static final int ACTION_NEXT_UPDATE_TIME = 2;
    private static final int ACTION_UPDATE_NOW = 3;

    private EpgStoredSettings mSettings;
    private Context context;

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
        context = getActivity();

        GuidedStepHelper.addAction(actions,
                context,
                ACTION_EPG_UPDATE_SERVICE,
                "Update Service",
                "IDLE");
        GuidedStepHelper.addAction(actions,
                context,
                ACTION_LAST_UPDATE_TIME,
                "Last Update Time",
                "");
        /* TODO: implement next update time
        GuidedStepHelper.addAction(actions,
                context,
                ACTION_NEXT_UPDATE_TIME,
                "Next Update Time",
                "");
        */
        GuidedStepHelper.addAction(actions,
                context,
                ACTION_UPDATE_NOW,
                "Update Now",
                "");
    }

    @Override
    public void onResume() {
        super.onResume();

        context = getActivity();

        // Setup broadcast listner to get status information from EPGUpdateService
        IntentFilter filter = new IntentFilter(Constants.BROADCAST_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, filter);

        // load settings
        mSettings = new EpgStoredSettings(getActivity());
        GuidedAction lastUpdateTimeAction = findActionById(ACTION_LAST_UPDATE_TIME);
        lastUpdateTimeAction.setDescription(mSettings.getLastUpdateTimeStr());
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        if (action.getId() == ACTION_UPDATE_NOW) {
            launchEPGUpdateService();
        }
    }

    private void launchEPGUpdateService() {
        // TODO: check if service is already running
        Log.d("LaunchEPGUpdateService", "launched epg update service");
        mServiceIntent = new Intent(context, EPGUpdateService.class);
        mServiceIntent.setData(Uri.parse(mSettings.getUrl()));
        context.startService(mServiceIntent);
    }

    private void setLastUpdateTime() {
        Date now = new Date();
        mSettings.setLastUpdateTime(now);

        GuidedAction lastUpdateAction = findActionById(ACTION_LAST_UPDATE_TIME);
        lastUpdateAction.setDescription(mSettings.getLastUpdateTimeStr());
        notifyActionChanged(findActionPositionById(ACTION_LAST_UPDATE_TIME));
    }

    // Define the callback for broadcast data received
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isAdded()) return;  // needed to add this otherwise we get
                                    // fragment not attached to activity after
                                    // leaving fragment and coming back again
            int statusCode = intent.getIntExtra(Constants.EXTENDED_DATA_STATUS,
                    Constants.STATE_ACTION_UNKNOWN);
            GuidedAction serviceAction = findActionById(ACTION_EPG_UPDATE_SERVICE);

            switch (statusCode) {
                case Constants.STATE_ACTION_STARTED:
                    serviceAction.setDescription(getString(R.string.status_start));
                    break;
                case Constants.STATE_ACTION_CONNECTING:
                    serviceAction.setDescription(getString(R.string.status_connect));
                    break;
                case Constants.STATE_ACTION_DOWNLOADING:
                    serviceAction.setDescription(getString(R.string.status_download));
                    break;
                case Constants.STATE_ACTION_DOWNLOAD_COMPLETE:
                    serviceAction.setDescription(getString(R.string.status_download_done));
                    break;
                case Constants.STATE_ACTION_FAILED:
                    serviceAction.setDescription(getString(R.string.status_fail));
                    break;
                case Constants.STATE_ACTION_SOCKET_CONNECTED:
                    serviceAction.setDescription("SENDING GUIDE DATA TO XMLTV SOCKET");
                    break;
                case Constants.STATE_ACTION_COMPLETE:
                    serviceAction.setDescription("SUCCESS");
                    setLastUpdateTime();
                    break;
                default:
                    serviceAction.setDescription("UNKNOWN");
            }
            notifyActionChanged(findActionPositionById(ACTION_EPG_UPDATE_SERVICE));
        }
    };
}
