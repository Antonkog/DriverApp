package com.abona_erp.driver.app.data.model;
  
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
  ACTIVITY(1),
  CONFIRMATION(2),
  DEVICE_PROFILE(3),
  UNDO_ACTIVITY(4);
  
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
