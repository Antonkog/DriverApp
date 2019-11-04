package com.abona_erp.driver.app.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class DoubleJsonDeserializer implements JsonDeserializer<Double> {

  @Override
  public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    try {
      return json.getAsDouble();
    } catch (Exception e) {
      return 0D;
    }
  }
}
