package com.abona_erp.driver.app.ui.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

public class ProgressBarDrawable extends Drawable {
  
  private int parts = 10;
  
  private Paint paint = null;
  private int fillColor = Color.parseColor("#27ae60");
  private int emptyColor = Color.parseColor("#999999");
  private int separatorColor = Color.parseColor("#F5F7FA");
  private RectF rectFill = null;
  private RectF rectEmpty = null;
  private List<RectF> separators = null;
  
  public ProgressBarDrawable(int parts) {
    this.parts = parts;
    this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    this.separators = new ArrayList<RectF>();
  }
  
  @Override
  protected boolean onLevelChange(int level)
  {
    invalidateSelf();
    return true;
  }
  
  @Override
  public void draw(Canvas canvas)
  {
    // Calculate values
    Rect b = getBounds();
    float width = b.width();
    float height = b.height();
    
    int spaceFilled = (int)(getLevel() * width / 10000);
    this.rectFill = new RectF(0, 0, spaceFilled, height);
    this.rectEmpty = new RectF(spaceFilled, 0, width, height);
    
    int spaceBetween = (int)(width / 100);
    int widthPart = (int)(width / this.parts - (int)(0.9 * spaceBetween));
    int startX = widthPart;
    for (int i=0; i<this.parts - 1; i++)
    {
      this.separators.add( new RectF(startX, 0, startX + spaceBetween, height) );
      startX += spaceBetween + widthPart;
    }
    
    
    // Foreground
    this.paint.setColor(this.fillColor);
    canvas.drawRect(this.rectFill, this.paint);
    
    // Background
    this.paint.setColor(this.emptyColor);
    canvas.drawRect(this.rectEmpty, this.paint);
    
    // Separator
    this.paint.setColor(this.separatorColor);
    for (RectF separator : this.separators)
    {
      canvas.drawRect(separator, this.paint);
    }
  }
  
  @Override
  public void setAlpha(int alpha)
  {
  }
  
  @Override
  public void setColorFilter(ColorFilter cf)
  {
  }
  
  @Override
  public int getOpacity()
  {
    return PixelFormat.TRANSLUCENT;
  }
}
