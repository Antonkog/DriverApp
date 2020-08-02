package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(DataType.Serializer.class)
public enum DataType {
  
  TASK(0),
  ALL_TASKS(1),
  COMPRESSED_TASK(2),
  ACTIVITY(20),
  UNDO_ACTIVITY(21),
  ALL_DELAY_REASONS(30),
  DELAY_REASONS(31),
  CONFIRMATION(40),
  DEVICE_PROFILE(60),
  VEHICLE(80),
  DOCUMENT(100),
  LINK_WITH_ABONA(200);
  
  int dataType;
  
  DataType(int dataType) {
    this.dataType = dataType;
  }
  
  static DataType getDataTypeByCode(int dataType) {
    for (DataType type : values()) {
      if (type.dataType == dataType) return type;
    }
    return TASK;
  }
  
  static class Serializer implements JsonSerializer<DataType>, JsonDeserializer<DataType> {
    
    @Override
    public JsonElement serialize(DataType src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.dataType);
    }
    
    @Override
    public DataType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      try {
        return getDataTypeByCode(json.getAsNumber().intValue());
      } catch (JsonParseException e) {
        return TASK;
      }
    }
  }
}
