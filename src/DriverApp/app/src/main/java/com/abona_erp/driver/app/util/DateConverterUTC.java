package com.abona_erp.driver.app.util;

import androidx.room.TypeConverter;

import com.abona_erp.driver.app.logging.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateConverterUTC {
  
  static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  
  @TypeConverter
  public synchronized static Date fromTimestamp(String value) {
    if (value != null) {
      try {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.parse(value);
      } catch (ParseException e) {
        Log.e("Error", e.getMessage());
      }
    }
    return null;
  }
  
  @TypeConverter
  public synchronized static String dateToTimestamp(Date value) {
    return value == null ? null : dateFormat.format(value);
  }
}
