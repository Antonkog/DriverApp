package com.abona_erp.driver.app.data.converters;

import androidx.room.TypeConverter;

public enum LogType {
  FCM(0),
  APP_TO_SERVER(1),
  SERVER_TO_APP(2);


  private final Integer code;
  
  LogType(Integer value) {
    this.code = value;
  }
  
  public Integer getCode() {
    return code;
  }
  
  @TypeConverter
  public static LogType getType(Integer numeral) {
    for (LogType lt : values()) {
      if (lt.code == numeral) {
        return lt;
      }
    }
    return null;
  }
  
  @TypeConverter
  public static Integer getTypeInt(LogType type) {
    if (type != null) {
      return type.code;
    }
    return null;
  }
}
