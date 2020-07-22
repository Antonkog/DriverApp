package com.abona_erp.driver.app.data.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(ActivityStatus.Serializer.class)
public enum ActivityStatus {
  
  PENDING(0),
  RUNNING(1),
  FINISHED(2);
  
  private int status;
  
  ActivityStatus(int status) {
    this.status = status;
  }
  
  static ActivityStatus getActivityStatusByCode(int status) {
    for (ActivityStatus activity : values()) {
      if (activity.status == status) {
        return activity;
      }
    }
    return PENDING;
  }
  
  static class Serializer implements JsonSerializer<ActivityStatus>, JsonDeserializer<ActivityStatus> {
    
    @Override
    public JsonElement serialize(ActivityStatus src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.status);
    }
    
    @Override
    public ActivityStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      try {
        return getActivityStatusByCode(json.getAsNumber().intValue());
      } catch (JsonParseException e) {
        return PENDING;
      }
    }
  }
}
