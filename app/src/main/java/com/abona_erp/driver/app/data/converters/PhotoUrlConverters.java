package com.abona_erp.driver.app.data.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PhotoUrlConverters {
  
  @TypeConverter
  public static ArrayList<String> fromPhotoUrl(String value) {
    Type listType = new TypeToken<ArrayList<String>>() {}.getType();
    return new Gson().fromJson(value, listType);
  }
  
  @TypeConverter
  public static String arrayListToString(ArrayList<String> list) {
    Gson gson = new Gson();
    String json = gson.toJson(list);
    
    return json;
  }
}
