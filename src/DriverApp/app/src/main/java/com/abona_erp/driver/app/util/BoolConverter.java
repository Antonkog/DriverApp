package com.abona_erp.driver.app.util;

import androidx.room.TypeConverter;

public class BoolConverter {

  @TypeConverter
  public static Integer fromBool(boolean value) {
    return value == false ? 0 : 1;
  }

  @TypeConverter
  public static boolean intToBool (Integer num) {
    return num == 0 ? false : true;
  }
}