package org.duckdns.altered.kellytv;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kmorning on 2018-02-24.
 */

public class EpgStoredSettings {
    private SharedPreferences mSettings;
    private SharedPreferences.Editor mEditor;
    private Context mContext;
    private boolean mAutoUpdate;
    private String mUrl;
    private int mIntervalValue;
    private String mIntervalUnits;
    // TODO: Figure out how to handle time
    //private String mLastUpdateTime;

    public EpgStoredSettings(Context context) {
        mContext = context;
        // handle to shared preferences
        mSettings = context.getSharedPreferences(context.getString(R.string.epg_settings_key),
                context.MODE_PRIVATE);
        mEditor = mSettings.edit();

        // load current shared preference and set defaults if key is not stored yet
        mAutoUpdate = mSettings.getBoolean(context.getString(R.string.epg_auto_update_key),
                context.getResources().getBoolean(R.bool.epg_auto_update_default));
        mUrl = mSettings.getString(context.getString(R.string.epg_url_key),
                context.getString(R.string.epg_url_default));
        mIntervalValue = mSettings.getInt(context.getString(R.string.epg_interval_value_key),
                context.getResources().getInteger(R.integer.epg_interval_value_default));
        mIntervalUnits = mSettings.getString(context.getString(R.string.epg_interval_units_key),
                context.getString(R.string.epg_interval_units_default));
    }

    public boolean getAutoUpdate() {
        return mAutoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        mAutoUpdate = autoUpdate;
        mEditor.putBoolean(mContext.getString(R.string.epg_auto_update_key), mAutoUpdate);
        mEditor.apply();
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
        mEditor.putString(mContext.getString(R.string.epg_url_key), mUrl);
        mEditor.apply();
    }

    public int getIntervalValue() {
        return mIntervalValue;
    }

    public void setIntervalValue(int value) {
        mIntervalValue = value;
        mEditor.putInt(mContext.getString(R.string.epg_interval_value_key), mIntervalValue);
        mEditor.apply();
    }

    public String getIntervalUnits() {
        return mIntervalUnits;
    }

    public void setIntervalUnits(String units) {
        mIntervalUnits = units;
        mEditor.putString(mContext.getString(R.string.epg_interval_units_key), mIntervalUnits);
        mEditor.apply();
    }

    public enum IntervalUnits {
        minutes,
        hours,
        days
    }
}