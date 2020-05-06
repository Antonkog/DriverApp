package com.abona_erp.driver.app.data.converters;

import androidx.room.TypeConverter;

public enum LogLevel {
  
  VERBOSE(0),
  SILENT(1),
  INFO(2),
  DEBUG(4),
  WARNING(8),
  ERROR(16),
  FATAL(32),
  ASSERT(64);
  
  private final Integer code;
  
  LogLevel(Integer value) {
    this.code = value;
  }
  
  public Integer getCode() {
    return code;
  }
  
  @TypeConverter
  public static LogLevel getLevel(Integer numeral) {
    for (LogLevel ll : values()) {
      if (ll.code == numeral) {
        return ll;
      }
    }
    return null;
  }
  
  @TypeConverter
  public static Integer getLevelInt(LogLevel level) {
    if (level != null) {
      return level.code;
    }
    return null;
  }
}
