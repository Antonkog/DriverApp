package com.abona_erp.driver.app.util;

import java.util.Calendar;
import java.util.Date;

public class AppUtils {
  
  public static Date getCurrentDateTime() {
    Date currentDate = Calendar.getInstance().getTime();
    return currentDate;
  }

  public static String parseOrderNo(int orderNo) {
    String _orderNo = String.valueOf(orderNo);
    String tmp = _orderNo.substring(0, 4);
    tmp += "/";
    tmp += _orderNo.substring(4, 6);
    tmp += "/";
    tmp += _orderNo.substring(6);
    return tmp;
  }
}
