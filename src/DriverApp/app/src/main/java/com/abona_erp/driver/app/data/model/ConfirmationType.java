package com.abona_erp.driver.app.data.model;


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
  
  static ConfirmationType getConfirmationTypeByCode(int confirmationType) {
    for (ConfirmationType type : values()) {
      if (type.confirmationType == confirmationType) return type;
    }
    return NOT_CONFIRMED;
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
