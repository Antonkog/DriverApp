package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(EnumNoteType.Serializer.class)
public enum EnumNoteType {
  
  STANDARD(0),
  HIGH(1);
  
  int noteType;
  
  EnumNoteType(int noteType) {
    this.noteType = noteType;
  }
  
  static EnumNoteType getNoteTypeByCode(int noteType) {
    for (EnumNoteType type : values()) {
      if (type.noteType == noteType)
        return type;
    }
    return STANDARD;
  }
  
  static class Serializer implements JsonSerializer<EnumNoteType>, JsonDeserializer<EnumNoteType> {
    
    @Override
    public JsonElement serialize(EnumNoteType src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.noteType);
    }
    
    @Override
    public EnumNoteType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      try {
        return getNoteTypeByCode(json.getAsNumber().intValue());
      } catch (JsonParseException e) {
        return STANDARD;
      }
    }
  }
}
