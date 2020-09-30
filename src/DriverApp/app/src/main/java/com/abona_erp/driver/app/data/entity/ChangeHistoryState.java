package com.abona_erp.driver.app.data.entity;

import androidx.room.TypeConverter;

public enum ChangeHistoryState {
  NO_SYNC(0),
  TO_BE_CONFIRMED_BY_APP(1), //gray
  TO_BE_CONFIRMED_BY_DRIVER(2),//orange: device got data, task not opened. App to server don't has orange. Only gray and green. 
  CONFIRMED(3),//green:  200 from server or driver open task
  ERROR(4);//red - we don't show to driver, i use to rewrite logs if attempts duplicate.


  private final Integer code;

  ChangeHistoryState(Integer value) {
    this.code = value;
  }
  
  public Integer getCode() {
    return code;
  }
  
  @TypeConverter
  public static ChangeHistoryState getType(Integer numeral) {
    for (ChangeHistoryState lt : values()) {
      if (lt.code == numeral) {
        return lt;
      }
    }
    return null;
  }
  
  @TypeConverter
  public static Integer getTypeInt(ChangeHistoryState type) {
    if (type != null) {
      return type.code;
    }
    return null;
  }
}
