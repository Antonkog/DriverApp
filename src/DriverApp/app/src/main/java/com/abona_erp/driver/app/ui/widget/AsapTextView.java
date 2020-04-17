package com.abona_erp.driver.app.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.abona_erp.driver.core.util.FontCache;

public class AsapTextView extends AppCompatTextView {
  
  private static final String font_name = "fonts/Asap_Medium.ttf";
  
  public AsapTextView(Context ctx) {
    super(ctx);
  }
  
  public AsapTextView(Context ctx, AttributeSet attrs) {
    super(ctx, attrs);
    applyCustomFont(ctx);
  }
  
  public AsapTextView(Context ctx, AttributeSet attrs, int defStyle) {
    super(ctx, attrs, defStyle);
    applyCustomFont(ctx);
  }
  
  private void applyCustomFont(Context ctx) {
    Typeface customFont = FontCache.getTypeface(font_name, ctx);
    setTypeface(customFont);
  }
}
