package com.redhotapp.driverapp.data.model.abona;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(SourceReference.Serializer.class)
public enum SourceReference {
  
  DRIVER_APP(0),
  ABONA(1);
  
  int sourceReference;
  
  SourceReference(int sourceReference) {
    this.sourceReference = sourceReference;
  }
  
  static SourceReference getSourceReferenceByCode(int sourceReference) {
    for (SourceReference reference : values()) {
      if (reference.sourceReference == sourceReference)
        return reference;
    }
    return DRIVER_APP;
  }
  
  static class Serializer implements JsonSerializer<SourceReference>, JsonDeserializer<SourceReference> {
    
    @Override
    public JsonElement serialize(SourceReference src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.sourceReference);
    }
    
    @Override
    public SourceReference deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      try {
        return getSourceReferenceByCode(json.getAsNumber().intValue());
      } catch (JsonParseException e) {
        return DRIVER_APP;
      }
    }
  }
}
