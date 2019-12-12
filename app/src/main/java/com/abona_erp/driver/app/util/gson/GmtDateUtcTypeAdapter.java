package com.abona_erp.driver.app.util.gson;

import com.abona_erp.driver.app.logging.Log;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GmtDateUtcTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
  
  private final DateFormat dateFormat;
  private final DateFormat dateFormatUtc;
  private final DateFormat dateFormat2;
  
  public GmtDateUtcTypeAdapter() {
    dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
      Locale.getDefault());
    
    dateFormatUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
      Locale.getDefault());
    dateFormatUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
    
    dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
      Locale.getDefault());
  }
  
  @Override
  public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
    synchronized (dateFormatUtc) {
      String dateFormatAsString = dateFormatUtc.format(date);
      Log.i("-------------", dateFormatAsString + " ************************************************");
      return new JsonPrimitive(dateFormatAsString);
    }
  }
  
  @Override
  public synchronized Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
    try {
      synchronized (dateFormat) {
        return dateFormat.parse(jsonElement.getAsString());
      }
    } catch (ParseException e) {
      try {
        synchronized (dateFormat2) {
          return dateFormat2.parse(jsonElement.getAsString());
        }
      } catch (ParseException e1) {
        throw new JsonSyntaxException(jsonElement.getAsString(), e);
      }
    }
  }
}
