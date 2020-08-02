package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(EnumPalletExchangeType.Serializer.class)
public enum EnumPalletExchangeType {
  
  NA(0),
  NO(1),
  YES(2);
  
  int palletExchangeType;
  
  EnumPalletExchangeType(int palletExchangeType) {
    this.palletExchangeType = palletExchangeType;
  }
  
  static EnumPalletExchangeType getPalletExchangeTypeByCode(int palletExchangeType) {
    for (EnumPalletExchangeType type : values()) {
      if (type.palletExchangeType == palletExchangeType)
        return type;
    }
    return NA;
  }
  
  static class Serializer implements JsonSerializer<EnumPalletExchangeType>, JsonDeserializer<EnumPalletExchangeType> {
    
    @Override
    public JsonElement serialize(EnumPalletExchangeType src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.palletExchangeType);
    }
    
    @Override
    public EnumPalletExchangeType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      try {
        return getPalletExchangeTypeByCode(json.getAsNumber().intValue());
      } catch (JsonParseException e) {
        return NA;
      }
    }
  }
}
