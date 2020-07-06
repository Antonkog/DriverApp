package com.abona_erp.driver.app.data.model;

import android.content.Context;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.ui.feature.main.Constants;
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

  public String getStringStatus(Context context){
    switch (taskActionType){
      case 0:
        return context.getResources().getString(R.string.action_type_pick_up);
      case 1:
        return context.getResources().getString(R.string.action_type_drop_off);
      case 2:
        return context.getResources().getString(R.string.action_type_general);
      case 3:
        return context.getResources().getString(R.string.action_type_tractor_swap);
      case 4:
        return context.getResources().getString(R.string.action_type_delay);
      case 5:
        return context.getResources().getString(R.string.action_type_unknown);
      default: return Constants.empty_str;
    }
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
