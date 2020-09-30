package com.abona_erp.driver.app.data.entity;

import androidx.room.TypeConverter;

public enum ActionType {
  START_ACTIVITY(0),
  FINISH_ACTIVITY(1),
  UPDATE_TASK(2),
  DOCUMENT_UPLOAD(3);


  private final Integer code;

  ActionType(Integer value) {
    this.code = value;
  }
  
  public Integer getCode() {
    return code;
  }
  
  @TypeConverter
  public static ActionType getType(Integer numeral) {
    for (ActionType lt : values()) {
      if (lt.code == numeral) {
        return lt;
      }
    }
    return null;
  }
  
  @TypeConverter
  public static Integer getTypeInt(ActionType type) {
    if (type != null) {
      return type.code;
    }
    return null;
  }
}
