package com.abona_erp.driver.core.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.abona_erp.driver.core.base.multidex.BaseMultiDexInstaller;

/**
 * Basic application functionality that should be shared among all
 * Driver App applications.
 */
public class BaseApplication extends Application {
  private static final String TAG = BaseApplication.class.getSimpleName();
  
  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    assert getBaseContext() != null;
    checkAppBeingReplaced();
    if (BuildConfig.isMultidexEnabled()) {
      BaseMultiDexInstaller.install(this);
    }
  }
  
  /**
   * Ensure this application object is not out-of-date.
   */
  private void checkAppBeingReplaced() {
    // During app update the old apk can still be triggered by broadcasts
    // and spin up an out-of-date application. Kill old applications in this
    // bad state.
    if (null == getResources()) {
      Log.e(TAG, "getResources() null, closing app.");
      System.exit(0);
    }
  }
}
