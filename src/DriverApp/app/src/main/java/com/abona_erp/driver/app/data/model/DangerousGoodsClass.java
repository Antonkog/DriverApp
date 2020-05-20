package com.abona_erp.driver.app.data.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;

@JsonAdapter(DangerousGoodsClass.Serializer.class)
public enum DangerousGoodsClass {
  
  NA(0),
  CLASS_1_EXPLOSIVES(10),
  CLASS_1_1_EXPLOSIVES(11),
  CLASS_1_2_EXPLOSIVES(12),
  CLASS_1_3_EXPLOSIVES(13),
  CLASS_1_4_EXPLOSIVES(14),
  CLASS_1_5_EXPLOSIVES(15),
  CLASS_1_6_EXPLOSIVES(16),
  CLASS_2_FLAMMABLE_GAS(20),
  CLASS_2_NON_FLAMMABLE_GAS(21),
  CLASS_2_POISON_GAS(22),
  CLASS_3_FLAMMABLE_LIQUID(30),
  CLASS_4_1_FLAMMABLE_SOLIDS(41),
  CLASS_4_2_SPONTANEOUSLY_COMBUSTIBLE(42),
  CLASS_4_3_DANGEROUSE_WHEN_WET(43),
  CLASS_5_1_OXIDIZER(51),
  CLASS_5_2_ORAGNIC_PEROXIDES(52),
  CLASS_6_1_POISON(61),
  CLASS_6_2_INFECTIOUS_SUBSTANCE(62),
  CLASS_7_FISSILE(70),
  CLASS_7_RADIOACTIVE_I(71),
  CLASS_7_RADIOACTIVE_II(72),
  CLASS_7_RADIOACTIVE_III(73),
  CLASS_8_CORROSIVE(80),
  CLASS_9_MISCELLANEOUS(90);
  
  int dangerousGoodsClass;
  
  DangerousGoodsClass(int dangerousGoodsClass) {
    this.dangerousGoodsClass = dangerousGoodsClass;
  }
  
  static DangerousGoodsClass getDangerousGoodsClassByCode(int dangerousGoodsClass) {
    for (DangerousGoodsClass clazz : values()) {
      if (clazz.dangerousGoodsClass == dangerousGoodsClass)
        return clazz;
    }
    return NA;
  }
  
  static class Serializer implements JsonSerializer<DangerousGoodsClass>, JsonDeserializer<DangerousGoodsClass> {
    
    @Override
    public JsonElement serialize(DangerousGoodsClass src, Type typeOfSrc, JsonSerializationContext context) {
      return context.serialize(src.dangerousGoodsClass);
    }
    
    @Override
    public DangerousGoodsClass deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
      try {
        return getDangerousGoodsClassByCode(json.getAsNumber().intValue());
      } catch (JsonParseException e) {
        return NA;
      }
    }
  }
}
