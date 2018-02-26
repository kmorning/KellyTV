package org.duckdns.altered.kellytv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

/**
 * Created by kmorning on 2018-02-25.
 */

public class EpgStatusFragment extends GuidedStepFragment {
    /* Action ID definition */
    private static final int ACTION_EPG_UPDATE_SERVICE = 0;
    private static final int ACTION_LAST_UPDATE_TIME = 1;
    private static final int ACTION_NEXT_UPDATE_TIME = 2;
    private static final int ACTION_UPDATE_NOW = 3;

    private EpgStoredSettings mSettings;

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

        // Setup broadcast listner to get status information from EPGUpdateService
        IntentFilter filter = new IntentFilter(Constants.BROADCAST_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, filter);

        GuidedStepHelper.addAction(actions,
                context,
                ACTION_EPG_UPDATE_SERVICE,
                "Update Service",
                "");
        GuidedStepHelper.addAction(actions,
                context,
                ACTION_LAST_UPDATE_TIME,
                "Last Update Time",
                "");
        GuidedStepHelper.addAction(actions,
                context,
                ACTION_NEXT_UPDATE_TIME,
                "Next Update Time",
                "");
        GuidedStepHelper.addAction(actions,
                context,
                ACTION_UPDATE_NOW,
                "Update Now",
                "");
    }

    @Override
    public void onResume() {
        super.onResume();
        // load settings
        mSettings = new EpgStoredSettings(getActivity());
        GuidedAction lastUpdateTimeAction = findActionById(ACTION_LAST_UPDATE_TIME);
        lastUpdateTimeAction.setDescription(mSettings.getLastUpdateTimeStr());
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {

    }

    // Define the callback for broadcast data received
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int statusCode = intent.getIntExtra(Constants.EXTENDED_DATA_STATUS,
                    Constants.STATE_ACTION_UNKNOWN);
            //TextView textView = findViewById(R.id.textView);

            switch (statusCode) {
                case Constants.STATE_ACTION_STARTED:
                    //textView.setText(getString(R.string.status_start));
                    break;
                case Constants.STATE_ACTION_CONNECTING:
                    //textView.setText(getString(R.string.status_connect));
                    break;
                case Constants.STATE_ACTION_DOWNLOADING:
                    //textView.setText(getString(R.string.status_download));
                    break;
                case Constants.STATE_ACTION_DOWNLOAD_COMPLETE:
                    //textView.setText(getString(R.string.status_download_done));
                    break;
                case Constants.STATE_ACTION_FAILED:
                    //textView.setText(getString(R.string.status_fail));
                    break;
                default:
                    //textView.setText("UNKNOWN");
            }
        }
    };
}
