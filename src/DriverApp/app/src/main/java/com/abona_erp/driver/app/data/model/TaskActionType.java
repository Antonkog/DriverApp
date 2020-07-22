package com.abona_erp.driver.app.data.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(TaskActionType.Serializer.class)
public enum TaskActionType {
  
  PICK_UP(0),
  DROP_OFF(1),
  GENERAL(2),
  TRACTOR_SWAP(3),
  DELAY(4),
  UNKNOWN(100);
  
  int taskActionType;
  
  TaskActionType(int taskActionType) {
    this.taskActionType = taskActionType;
  }
  
  static TaskActionType getTaskActionTypeByCode(int taskActionType) {
    for (TaskActionType type : values()) {
      if (type.taskActionType == taskActionType) return type;
    }
    return UNKNOWN;
  }
  
  static class Serializer implements JsonSerializer<TaskActionType>, JsonDeserializer<TaskActionType> {
    
    @Override
    public JsonElement serialize(TaskActionType src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.taskActionType);
    }
    
    @Override
    public TaskActionType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      try {
        return getTaskActionTypeByCode(json.getAsNumber().intValue());
      } catch (JsonParseException e) {
        return UNKNOWN;
      }
    }
  }
}
