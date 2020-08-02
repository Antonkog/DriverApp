package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(EnumNumberType.Serializer.class)
public enum EnumNumberType {
  
  PHONE(0),
  MOBILE(1),
  EMAIL(2);
  
  int numberType;
  
  EnumNumberType(int numberType) {
    this.numberType = numberType;
  }
  
  static EnumNumberType getNumberTypeByCode(int numberType) {
    for (EnumNumberType type : values()) {
      if (type.numberType == numberType)
        return type;
    }
    return PHONE;
  }
  
  static class Serializer implements JsonSerializer<EnumNumberType>, JsonDeserializer<EnumNumberType> {
    
    @Override
    public JsonElement serialize(EnumNumberType src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.numberType);
    }
    
    @Override
    public EnumNumberType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      try {
        return getNumberTypeByCode(json.getAsNumber().intValue());
      } catch (JsonParseException e) {
        return PHONE;
      }
    }
  }
}
