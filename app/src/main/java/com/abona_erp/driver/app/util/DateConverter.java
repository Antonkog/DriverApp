package com.abona_erp.driver.app.util;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
  
  static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  
  @TypeConverter
  public synchronized static Date fromTimestamp(String value) {
    if (value != null) {
      try {
        return df.parse(value);
      } catch (ParseException ignore) {
      }
      return null;
    } else {
      return null;
    }
  }
  
  @TypeConverter
  public synchronized static String dateToTimestamp(Date value) {
    return value == null ? null : df.format(value);
  }
}
