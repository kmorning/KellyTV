package org.duckdns.altered.kellytv;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

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
    private String mLastUpdateTimeStr;

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
        mLastUpdateTimeStr = mSettings.getString(context.getString(R.string.epg_last_update_time_key),
                context.getString(R.string.epg_last_update_time_default));
        //mLastUpdateTime = stringToDate(mLastUpdateTimeStr);
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

    public Date getLastUpdateTime() {
        return stringToDate(mLastUpdateTimeStr);
    }

    public String getLastUpdateTimeStr() {
        return mLastUpdateTimeStr;
    }

    public void setLastUpdateTime(Date date) {
        mLastUpdateTimeStr = dateToString(date);
        mEditor.putString(mContext.getString(R.string.epg_last_update_time_key), mLastUpdateTimeStr);
        mEditor.apply();
    }

    public void restoreDefaults() {
        mAutoUpdate = mContext.getResources().getBoolean(R.bool.epg_auto_update_default);
        mEditor.putBoolean(mContext.getString(R.string.epg_auto_update_key), mAutoUpdate);
        mUrl = mContext.getString(R.string.epg_url_default);
        mEditor.putString(mContext.getString(R.string.epg_url_key), mUrl);
        mIntervalValue = mContext.getResources().getInteger(R.integer.epg_interval_value_default);
        mEditor.putInt(mContext.getString(R.string.epg_interval_value_key), mIntervalValue);
        mIntervalUnits = mContext.getString(R.string.epg_interval_units_default);
        mEditor.putString(mContext.getString(R.string.epg_interval_units_key), mIntervalUnits);
        mEditor.apply();
    }

    public long getIntervalMillis() {
        long multiplier;
        if (mIntervalUnits.equals("days")) {
            multiplier = 24 * 60 * 60 * 1000;
        } else if (mIntervalUnits.equals("hours")) {
            multiplier = 60 * 60 * 1000;
        } else {
            multiplier = 60 * 1000;
        }
        return mIntervalValue * multiplier;
    }

    public enum IntervalUnits {
        minutes,
        hours,
        days
    }

    private Date stringToDate(String dateStr) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        Date date;
        try {
            date = ft.parse(dateStr);
        } catch (ParseException e) {
            date = new Date();
            date.setTime(0);
        }
        return date;
    }

    private String dateToString(Date date) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        return ft.format(date);
    }
}