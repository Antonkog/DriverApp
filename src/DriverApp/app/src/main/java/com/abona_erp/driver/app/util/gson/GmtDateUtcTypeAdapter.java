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
  private final DateFormat dateFormat_2;
  private final DateFormat dateFormat_3;
  
  public GmtDateUtcTypeAdapter() {
    dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    dateFormat_2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    dateFormat_3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    dateFormat_2.setTimeZone(TimeZone.getTimeZone("UTC"));
    dateFormat_3.setTimeZone(TimeZone.getTimeZone("UTC"));
  }
  
  @Override
  public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
    synchronized (dateFormat) {
      String dateFormatAsString = dateFormat.format(date);
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
        synchronized (dateFormat_2) {
          return dateFormat_2.parse(jsonElement.getAsString());
        }
      } catch (ParseException e1) {
        try {
          synchronized (dateFormat_3) {
            return dateFormat_3.parse(jsonElement.getAsString());
          }
        } catch (ParseException e2) {
          throw new JsonSyntaxException(jsonElement.getAsString(), e2);
        }
      }
    }
  }
}
