package com.abona_erp.driver.app.data.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(TaskStatus.Serializer.class)
public enum TaskStatus {
  
  PENDING(0),
  RUNNING(50),
  BREAK(51),
  CMR(90),
  FINISHED(100);
  
  int taskStatus;
  
  TaskStatus(int taskStatus) {
    this.taskStatus = taskStatus;
  }
  
  static TaskStatus getTaskStatusByCode(int taskStatus) {
    for (TaskStatus status : values()) {
      if (status.taskStatus == taskStatus)
        return status;
    }
    return PENDING;
  }
  
  static class Serializer implements JsonSerializer<TaskStatus>, JsonDeserializer<TaskStatus> {
    
    @Override
    public JsonElement serialize(TaskStatus src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.taskStatus);
    }
    
    @Override
    public TaskStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      try {
        return getTaskStatusByCode(json.getAsNumber().intValue());
      } catch (JsonParseException e) {
        return PENDING;
      }
    }
  }
}
