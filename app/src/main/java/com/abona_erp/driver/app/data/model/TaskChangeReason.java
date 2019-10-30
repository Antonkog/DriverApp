package com.abona_erp.driver.app.data.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(TaskChangeReason.Serializer.class)
public enum TaskChangeReason {
  
  CREATED(0),
  UPDATED_ABONA(1),
  UPDATED_APP(2),
  DELETED(3);
  
  int taskChangeReason;
  
  TaskChangeReason(int taskChangeReason) {
    this.taskChangeReason = taskChangeReason;
  }
  
  static TaskChangeReason getTaskChangeReasonByCode(int taskChangeReason) {
    for (TaskChangeReason reason : values()) {
      if (reason.taskChangeReason == taskChangeReason)
        return reason;
    }
    return CREATED;
  }
  
  static class Serializer implements JsonSerializer<TaskChangeReason>,
    JsonDeserializer<TaskChangeReason> {
    
    @Override
    public JsonElement serialize(TaskChangeReason src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.taskChangeReason);
    }
    
    @Override
    public TaskChangeReason deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      try {
        return getTaskChangeReasonByCode(json.getAsNumber().intValue());
      } catch (JsonParseException e) {
        return CREATED;
      }
    }
  }
}
