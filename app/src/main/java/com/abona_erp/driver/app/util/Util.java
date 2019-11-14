package com.abona_erp.driver.app.util;

import com.abona_erp.driver.app.BuildConfig;

public class Util {
  private static final String TAG = Util.class.getSimpleName();
  
  /**
   * The app version.
   * <p>
   *   This code should be used in all places that compare app versions
   *   rather than {@link BuildConfig#VERSION_CODE}.
   * </p>
   */
  public static int getCanonicalVersionCode() {
    return BuildConfig.CANONICAL_VERSION_CODE;
  }
}
