package com.abona_erp.driver.core.util;

/**
 * A collection of miscellaneous utility functions.
 */
public class MiscUtil {
  
  private MiscUtil() {
  }
  
  /**
   * Returns the Tag for a class to be used in Android logging statements.
   */
  public static String getTag(Object o) {
    if (o instanceof Class<?>) {
      return "DriverApp" + "." + ((Class<?>)o).getSimpleName();
    }
    return "DriverApp" + "." + o.getClass().getSimpleName();
  }
}
