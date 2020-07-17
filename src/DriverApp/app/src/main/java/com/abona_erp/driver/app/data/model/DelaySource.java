package com.abona_erp.driver.app.data.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(DelaySource.Serializer.class)
public enum DelaySource {
  
  NA(0),
  DISPATCHER(1),
  CUSTOMER(2),
  DRIVER(3);
  
  private int value;
  
  DelaySource(int delaySource) {
    this.value = delaySource;
  }
  
  static DelaySource getDelaySourceByCode(int delaySource) {
    for (DelaySource ds : values()) {
      if (ds.value == delaySource) {
        return ds;
      }
    }
    return NA;
  }
  
  static class Serializer implements JsonSerializer<DelaySource>, JsonDeserializer<DelaySource> {
    
    @Override
    public JsonElement serialize(DelaySource src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.value);
    }
    
    @Override
    public DelaySource deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      try {
        return getDelaySourceByCode(json.getAsNumber().intValue());
      } catch (JsonParseException e) {
        return NA;
      }
    }
  }
}
