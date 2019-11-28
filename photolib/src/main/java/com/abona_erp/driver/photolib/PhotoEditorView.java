package com.abona_erp.driver.photolib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;

public class PhotoEditorView extends RelativeLayout {
  
  private static final String TAG = "PhotoEditorView";
  
  private PhotoImageView mImgSource;
  private BrushDrawingView mBrushDrawingView;
  private static final int imgSrcId = 1, brushSrcId = 2;
  
  public PhotoEditorView(Context context) {
    super(context);
    init(null);
  }
  
  public PhotoEditorView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs);
  }
  
  public PhotoEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs);
  }
  
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public PhotoEditorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(attrs);
  }
  
  @SuppressLint("Recycle")
  private void init(@Nullable AttributeSet attrs) {
    // Setup image attributes:
    mImgSource = new PhotoImageView(getContext());
    mImgSource.setId(imgSrcId);
    mImgSource.setAdjustViewBounds(true);
    RelativeLayout.LayoutParams imgSrcParam = new RelativeLayout.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    imgSrcParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
    if (attrs != null) {
      TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.PhotoEditorView);
      Drawable imgSrcDrawable = ta.getDrawable(R.styleable.PhotoEditorView_photo_src);
      if (imgSrcDrawable != null) {
        mImgSource.setImageDrawable(imgSrcDrawable);
      }
    }
    
    // Setup brush view:
    mBrushDrawingView = new BrushDrawingView(getContext());
    mBrushDrawingView.setVisibility(GONE);
    mBrushDrawingView.setId(brushSrcId);
  
    //Align brush to the size of image view
    RelativeLayout.LayoutParams brushParam = new RelativeLayout.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    brushParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
    brushParam.addRule(RelativeLayout.ALIGN_TOP, imgSrcId);
    brushParam.addRule(RelativeLayout.ALIGN_BOTTOM, imgSrcId);
    
    mImgSource.setOnImageChangedListener(new PhotoImageView.OnImageChangedListener() {
      @Override
      public void onBitmapLoaded(@Nullable Bitmap sourceBitmap) {
    
      }
    });
    
    addView(mImgSource, imgSrcParam);
    
    // Add brush view:
    addView(mBrushDrawingView, brushParam);
  }
  
  public AppCompatImageView getSource() {
    return mImgSource;
  }
  
  BrushDrawingView getBrushDrawingView() {
    return mBrushDrawingView;
  }
}
