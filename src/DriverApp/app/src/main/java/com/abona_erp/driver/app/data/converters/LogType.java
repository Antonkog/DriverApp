package com.abona_erp.driver.app.data.converters;

import androidx.room.TypeConverter;

public enum LogType {
  
  FCM(0),
  CONFIRMATION(1),
  API(2),
  INFO(3),
  MESSAGE(4),
  WARNING(5),
  ERROR(6),
  LOG(7);
  
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
