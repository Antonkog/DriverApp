package com.abona_erp.driver.app.data.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(SpecialFunction.Serializer.class)
public enum SpecialFunction {
  
  STANDARD(0),
  TAKE_IMAGES_CMR(1),
  SCAN_BARCODE(2),
  TAKE_IMAGES_SHIPMENT(3);
  
  int specialFunction;
  
  SpecialFunction(int specialFunction) {
    this.specialFunction = specialFunction;
  }
  
  static SpecialFunction getSpecialFunctionByCode(int specialFunction) {
    for (SpecialFunction sp : values()) {
      if (sp.specialFunction == specialFunction)
        return sp;
    }
    return STANDARD;
  }
  
  public static int getCodeBySpecialFunction(SpecialFunction specialFunction) {
    for (SpecialFunction sp : values()) {
      if (sp == specialFunction)
        return sp.specialFunction;
    }
    return -1;
  }
  
  static class Serializer implements JsonSerializer<SpecialFunction>, JsonDeserializer<SpecialFunction> {
    
    @Override
    public JsonElement serialize(SpecialFunction src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.specialFunction);
    }
    
    @Override
    public SpecialFunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      try {
        return getSpecialFunctionByCode(json.getAsNumber().intValue());
      } catch (JsonParseException e) {
        return STANDARD;
      }
    }
  }
}
