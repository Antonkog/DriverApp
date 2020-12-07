package com.abona_erp.driver.app.data.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(SpecialFunctionOperationType.Serializer.class)
public enum SpecialFunctionOperationType {
  
  ON_START_OF_ACTIVITY(0),
  ON_FINISH_OF_ACTIVITY(1),
  SPECIAL_FUNCTION_ONLY(2);
  
  int operationType;
  
  SpecialFunctionOperationType(int operationType) {
    this.operationType = operationType;
  }
  
  static SpecialFunctionOperationType getSpecialFunctionOperationTypeByCode(int operationType) {
    for (SpecialFunctionOperationType sp : values()) {
      if (sp.operationType == operationType)
        return sp;
    }
    return SPECIAL_FUNCTION_ONLY;
  }
  
  public static int getCodeBySpecialFunctionOperationType(SpecialFunctionOperationType operationType) {
    for (SpecialFunctionOperationType sp : values()) {
      if (sp == operationType)
        return sp.operationType;
    }
    return -1;
  }
  
  static class Serializer implements JsonSerializer<SpecialFunctionOperationType>, JsonDeserializer<SpecialFunctionOperationType> {
    
    @Override
    public JsonElement serialize(SpecialFunctionOperationType src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.operationType);
    }
    
    @Override
    public SpecialFunctionOperationType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      try {
        return getSpecialFunctionOperationTypeByCode(json.getAsNumber().intValue());
      } catch (JsonParseException e) {
        return SPECIAL_FUNCTION_ONLY;
      }
    }
  }
}
