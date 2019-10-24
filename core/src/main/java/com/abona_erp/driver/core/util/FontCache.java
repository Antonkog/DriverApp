package com.abona_erp.driver.core.util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

public class FontCache {
  
  private static HashMap<String, Typeface> fontCache = new HashMap<>();
  
  public static Typeface getTypeface(String fontname, Context context) {
    Typeface typeface = fontCache.get(fontname);
    
    if (null == typeface) {
      try {
        typeface = Typeface.createFromAsset(context.getAssets(), fontname);
      } catch (Exception e) {
        return null;
      }
      
      fontCache.put(fontname, typeface);
    }
    
    return typeface;
  }
}
