package com.abona_erp.driver.app.data.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(EnumContactType.Serializer.class)
public enum EnumContactType {
  
  VERFOLGER(0),
  BACKUP_VERFOLGER(1),
  CUSTOMER(2);
  
  int contactType;
  
  EnumContactType(int contactType) {
    this.contactType = contactType;
  }
  
  static EnumContactType getContactTypeByCode(int contactType) {
    for (EnumContactType type : values()) {
      if (type.contactType == contactType)
        return type;
    }
    return CUSTOMER;
  }
  
  static class Serializer implements JsonSerializer<EnumContactType>, JsonDeserializer<EnumContactType> {
    
    @Override
    public JsonElement serialize(EnumContactType src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.contactType);
    }
    
    @Override
    public EnumContactType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      try {
        return getContactTypeByCode(json.getAsNumber().intValue());
      } catch (JsonParseException e) {
        return CUSTOMER;
      }
    }
  }
}
