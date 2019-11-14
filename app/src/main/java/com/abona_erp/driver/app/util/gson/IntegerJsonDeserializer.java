package com.abona_erp.driver.app.util.gson;

import com.abona_erp.driver.app.logging.Log;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class IntegerJsonDeserializer implements JsonDeserializer<Integer> {
  
  @Override
  public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    try {
      return json.getAsInt();
    } catch (Exception e) {
      Log.e("IntegerJsonDeserializer", json != null ? json.toString() : "");
      return 0;
    }
  }
}
