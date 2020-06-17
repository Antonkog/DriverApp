package com.abona_erp.driver.photolib;

import android.graphics.Bitmap;

public interface OnSaveBitmap {
  void onBitmapReady(Bitmap saveBitmap);
  void onFailure(Exception e);
}
