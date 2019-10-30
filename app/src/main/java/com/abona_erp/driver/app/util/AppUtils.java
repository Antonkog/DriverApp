package com.abona_erp.driver.app.util;

import java.util.Calendar;
import java.util.Date;

public class AppUtils {
  
  public static Date getCurrentDateTime() {
    Date currentDate = Calendar.getInstance().getTime();
    return currentDate;
  }
}
