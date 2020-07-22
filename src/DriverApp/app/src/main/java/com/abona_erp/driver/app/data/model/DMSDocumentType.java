package com.abona_erp.driver.app.data.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(DMSDocumentType.Serializer.class)
public enum DMSDocumentType {
  
  NA(0),
  POD(24),
  CMR(25),
  PALLETS_NOTE(26),
  SAFETY_CERTIFICATE(27),
  SHIPMENT_IMAGE(28),
  DAMAGED_SHIPMENT_IMAGE(29),
  DAMAGED_VEHICLE_IMAGE(30);
  
  int documentType;
  
  DMSDocumentType(int documentType) {
    this.documentType = documentType;
  }
  
  static DMSDocumentType getDocumentTypeByCode(int documentType) {
    for (DMSDocumentType type : values()) {
      if (type.documentType == documentType)
        return type;
    }
    return NA;
  }
  
  static class Serializer implements JsonSerializer<DMSDocumentType>, JsonDeserializer<DMSDocumentType> {
    
    @Override
    public JsonElement serialize(DMSDocumentType src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.documentType);
    }
    
    @Override
    public DMSDocumentType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      try {
        return getDocumentTypeByCode(json.getAsNumber().intValue());
      } catch (JsonParseException e) {
        return NA;
      }
    }
  }
}
