package org.duckdns.altered.kellytv;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by kmorning on 2018-02-17.
 */

public class MainFragment extends BrowseFragment {
    private static final String TAG = MainFragment.class.getSimpleName();

    private ArrayObjectAdapter mRowsAdapter;
    private static final int GRID_ITEM_WIDTH = 300;
    private static final int GRID_ITEM_HEIGHT = 200;

    @Override
    public void onActivityCreated(Bundle savedInsanceState) {
        Log.i(TAG, "onActivityCreate");
        super.onActivityCreated(savedInsanceState);

        setupUIElements();

        loadRows();
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

    private void loadRows() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        /* EPGItemPresenter */
        HeaderItem epgItemPresenterHeader = new HeaderItem(0, "EPG");

        GridItemPresenter mEpgPresenter = new GridItemPresenter();
        ArrayObjectAdapter epgRowAdapter = new ArrayObjectAdapter(mEpgPresenter);
        epgRowAdapter.add("Status");
        epgRowAdapter.add("Settings");
        mRowsAdapter.add(new ListRow(epgItemPresenterHeader, epgRowAdapter));

        /* set */
        setAdapter(mRowsAdapter);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            // each time the item is clicked, code inside of here will be executed.
            Log.d("itemClicked", item.getClass().toString());
            if (item instanceof String){
                Log.d("itemPressed", item.toString());
                if (item == "Settings") {
                    Intent intent = new Intent(getActivity(), EpgSettingsActivity.class);
                    startActivity(intent);
                }
            }
        }
    }

    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(getResources().getColor(R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {

        }
    }
}