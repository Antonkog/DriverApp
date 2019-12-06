package com.abona_erp.driver.app.util.gson;

import com.abona_erp.driver.app.logging.Log;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class BooleanJsonDeserializer implements JsonDeserializer<Boolean> {
  
  @Override
  public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    try {
      return json.getAsBoolean();
    } catch (Exception e) {
      e.printStackTrace();
      Log.e("BooleanJsonDeserializer", json != null ? json.toString() : "");
      return false;
    }
  }
}
