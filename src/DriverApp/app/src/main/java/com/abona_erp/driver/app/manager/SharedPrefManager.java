package com.abona_erp.driver.app.manager;

import android.content.Context;

import com.abona_erp.driver.core.util.CachedValue;

import java.util.HashSet;
import java.util.Set;

public class SharedPrefManager implements Manager {
  
  private static final String NAME = "sharedPrefs";
  
  private static final String KEY_FIRST_TIME_RUN = "firstTimeRun";
  private static final String KEY_FIREBASE_TOKEN = "firebaseToken";
  
  private Set<CachedValue> mCachedValues;
  
  private CachedValue<Boolean> mFirstTimeRun;
  private CachedValue<String> mFirebaseToken;
  
  @Override
  public void init(Context context) {
    CachedValue.initialize(context.getSharedPreferences(NAME, Context.MODE_PRIVATE));
    mCachedValues = new HashSet<>();
    mCachedValues.add(mFirstTimeRun = new CachedValue<>(KEY_FIRST_TIME_RUN, false, Boolean.class));
    mCachedValues.add(mFirebaseToken = new CachedValue<>(KEY_FIREBASE_TOKEN, String.class));
  }
  
  public boolean getFirstTimeRun() {
    return mFirstTimeRun.getValue();
  }
  
  public void setFirstTimeRun(final boolean firstTimeRun) {
    this.mFirstTimeRun.setValue(firstTimeRun);
  }
  
  public String getFirebaseToken() {
    return mFirebaseToken.getValue();
  }
  
  public void setFirebaseToken(final String firebaseToken) {
    this.mFirebaseToken.setValue(firebaseToken);
  }
  
  @Override
  public void clear() {
    for (CachedValue value : mCachedValues) {
      value.delete();
    }
  }
}
