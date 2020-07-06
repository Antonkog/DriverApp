package com.redhotapp.driverapp.util.preferences;

import android.util.Log;

public class LongPreference extends BasePreference<Long> {
	private static final String TAG = "LongPreference";
	private Long mMinValue;
	private Long mMaxValue;

	public LongPreference(String key, Long defaultValue) {
		super(key, defaultValue);
		init();
	}

	public LongPreference(String key, Long defaultValue, long minValue, long maxValue) {
		super(key, defaultValue);
		mMinValue = minValue;
		mMaxValue = maxValue;
		init();
	}

	@Override
	protected Long load() {
		Long result;
		try {
			if (getPrefs().contains(getKey())) {
				result = getPrefs().getLong(getKey(), -1);
			} else {
				result = getDefault();
			}
			if ((mMinValue != null && result < mMinValue) || (mMaxValue != null && result > mMaxValue)) {
				result = getDefault();
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			result = getDefault();
		}
		return result;
	}

	@Override
	protected void save() {
		getPrefs().edit().putLong(getKey(), get()).apply();
	}

	public Long getMin() {
		return mMinValue;
	}

	public Long getMax() {
		return mMaxValue;
	}
}
