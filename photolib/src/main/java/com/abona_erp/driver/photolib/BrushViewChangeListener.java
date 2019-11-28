package com.abona_erp.driver.photolib;

interface BrushViewChangeListener {
  void onViewAdd(BrushDrawingView brushDrawingView);
  
  void onViewRemoved(BrushDrawingView brushDrawingView);
  
  void onStartDrawing();
  
  void onStopDrawing();
}
