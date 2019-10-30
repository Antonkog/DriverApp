package com.abona_erp.driver.app.ui.widget.badges;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

public class BadgeSpan extends ReplacementSpan {
  
  private int badgeColor;
  private int textColor;
  private int radius;
  
  public BadgeSpan(int badgeColor, int textColor, int radius) {
    this.badgeColor = badgeColor;
    this.textColor = textColor;
    this.radius = radius;
  }
  
  @Override
  public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
    return (int)paint.measureText(text, start, end);
  }
  
  @Override
  public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
    RectF rect = new RectF(x, top, x + paint.measureText(text, start, end), bottom);
    paint.setColor(badgeColor);
    canvas.drawRoundRect(rect, radius, radius, paint);
    paint.setColor(textColor);
    canvas.drawText(text, start, end, x, y, paint);
  }
}
