package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(AppFileType.Serializer.class)
public enum AppFileType {
  
  JPG(0),
  PNG(1),
  PDF(2),
  NA(255);
  
  int appFileType;
  
  AppFileType(int appFileType) {
    this.appFileType = appFileType;
  }
  
  static AppFileType getAppFileTypeByCode(int appFileType) {
    for (AppFileType fileType : values()) {
      if (fileType.appFileType == appFileType)
        return fileType;
    }
    return NA;
  }
  
  static class Serializer implements JsonSerializer<AppFileType>, JsonDeserializer<AppFileType> {
    
    @Override
    public JsonElement serialize(AppFileType src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.appFileType);
    }
    
    @Override
    public AppFileType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      try {
        return getAppFileTypeByCode(json.getAsNumber().intValue());
      } catch (JsonParseException e) {
        return NA;
      }
    }
  }
}
