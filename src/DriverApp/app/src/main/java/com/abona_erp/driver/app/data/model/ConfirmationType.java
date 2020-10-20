package com.abona_erp.driver.app.data.model;


import android.content.Context;

import com.abona_erp.driver.app.R;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(ConfirmationType.Serializer.class)
public enum ConfirmationType {
  
  RECEIVED(0),
  TASK_CONFIRMED_BY_DEVICE(1),
  TASK_CONFIRMED_BY_USER(2),
  TASK_CONFIRMED_BY_ABONA(3),
  ACTIVITY_CONFIRMED_BY_DEVICE(20),
  ACTIVITY_CONFIRMED_BY_USER(21),
  ACTIVITY_CONFIRMED_BY_ABONA(22),
  NOT_CONFIRMED(100);
  
  int confirmationType;
  
  ConfirmationType(int confirmationType) {
    this.confirmationType = confirmationType;
  }
  
   public static ConfirmationType getConfirmationTypeByCode(int confirmationType) {
    for (ConfirmationType type : values()) {
      if (type.confirmationType == confirmationType) return type;
    }
    return NOT_CONFIRMED;
  }


  //because they all by ordinal every fragment, and now i can't refactor // A. Kogan
  public static ConfirmationType getConfirmationTypeByOrdinal(int confirmationTypeOrdinal) {
    for (ConfirmationType type : values()) {
      if (type.ordinal() == confirmationTypeOrdinal) return type;
    }
    return NOT_CONFIRMED;
  }


  public static String getNameByOrdinal(Context context, int confirmationTypeOrdinal) {
    switch (getConfirmationTypeByOrdinal(confirmationTypeOrdinal)) {
        case TASK_CONFIRMED_BY_DEVICE:
        case TASK_CONFIRMED_BY_USER:
        case TASK_CONFIRMED_BY_ABONA:
          return context.getResources().getString(R.string.sync_task);
        case ACTIVITY_CONFIRMED_BY_ABONA:
        case ACTIVITY_CONFIRMED_BY_DEVICE:
        case ACTIVITY_CONFIRMED_BY_USER:
          return context.getResources().getString(R.string.sync_activity);
      default:
        return "unknown sync data type" + getConfirmationTypeByOrdinal(confirmationTypeOrdinal).name();
    }
  }

  
  static class Serializer implements JsonSerializer<ConfirmationType>, JsonDeserializer<ConfirmationType> {
    
    @Override
    public JsonElement serialize(ConfirmationType src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.confirmationType);
    }
    
    @Override
    public ConfirmationType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      try {
        return getConfirmationTypeByCode(json.getAsNumber().intValue());
      } catch (JsonParseException e) {
        return NOT_CONFIRMED;
      }
    }
  }
}
