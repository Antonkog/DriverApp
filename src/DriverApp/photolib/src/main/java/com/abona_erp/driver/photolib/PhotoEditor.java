package com.abona_erp.driver.photolib;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.AppCompatImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PhotoEditor implements BrushViewChangeListener {
  
  private static final String TAG = "PhotoEditor";
  
  private final LayoutInflater mLayoutInflater;
  private Context context;
  private PhotoEditorView parentView;
  private AppCompatImageView imageView;
  private View deleteView;
  private BrushDrawingView brushDrawingView;
  private List<View> addedViews;
  private List<View> redoViews;
  private OnPhotoEditorListener mOnPhotoEditorListener;
  private boolean isTextPinchZoomable;
  private Typeface mDefaultTextTypeface;
  
  private PhotoEditor(Builder builder) {
    this.context = builder.context;
    this.parentView = builder.parentView;
    this.imageView = builder.imageView;
    this.deleteView = builder.deleteView;
    this.brushDrawingView = builder.brushDrawingView;
    this.isTextPinchZoomable = builder.isTextPinchZoomable;
    this.mDefaultTextTypeface = builder.textTypeface;
    
    mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    brushDrawingView.setBrushViewChangeListener(this);
    
    addedViews = new ArrayList<>();
    redoViews = new ArrayList<>();
  }
  
  @SuppressLint("ClickableViewAccessibility")
  public void addText(String text, final int colorCodeTextView) {
    addText(null, text, colorCodeTextView);
  }
  
  @SuppressLint("ClickableViewAccessibility")
  public void addText(@Nullable Typeface textTypeface, String text, final int colorCodeTextView) {
    final TextStyleBuilder styleBuilder = new TextStyleBuilder();
    
    styleBuilder.withTextColor(colorCodeTextView);
    if (textTypeface != null) {
      styleBuilder.withTextFont(textTypeface);
    }
    
    addText(text, styleBuilder);
  }
  
  @SuppressLint("ClickableViewAccessibility")
  public void addText(String text, @Nullable TextStyleBuilder styleBuilder) {
    brushDrawingView.setBrushDrawingMode(false);
    final View textRootView = getLayout(ViewType.TEXT);
    final TextView textInputTv = textRootView.findViewById(R.id.tvPhotoEditorText);
    final ImageView imgClose = textRootView.findViewById(R.id.imgPhotoEditorClose);
    final FrameLayout frmBorder = textRootView.findViewById(R.id.frmBorder);
  
    textInputTv.setText(text);
    if (styleBuilder != null)
      styleBuilder.applyStyle(textInputTv);
  
    MultiTouchListener multiTouchListener = getMultiTouchListener();
    multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
      @Override
      public void onClick() {
        boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
        frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
        imgClose.setVisibility(isBackgroundVisible ? View.GONE : View.VISIBLE);
        frmBorder.setTag(!isBackgroundVisible);
      }
    
      @Override
      public void onLongClick() {
        String textInput = textInputTv.getText().toString();
        int currentTextColor = textInputTv.getCurrentTextColor();
        if (mOnPhotoEditorListener != null) {
          mOnPhotoEditorListener.onEditTextChangeListener(textRootView, textInput, currentTextColor);
        }
      }
    });
  
    textRootView.setOnTouchListener(multiTouchListener);
    addViewToParent(textRootView, ViewType.TEXT);
  }
  
  public void editText(@NonNull View view, String inputText, @NonNull int colorCode) {
    editText(view, null, inputText, colorCode);
  }
  
  public void editText(@NonNull View view, @Nullable Typeface textTypeface, String inputText, @NonNull int colorCode) {
    final TextStyleBuilder styleBuilder = new TextStyleBuilder();
    styleBuilder.withTextColor(colorCode);
    if (textTypeface != null) {
      styleBuilder.withTextFont(textTypeface);
    }
    
    editText(view, inputText, styleBuilder);
  }
  
  public void editText(@NonNull View view, String inputText, @Nullable TextStyleBuilder styleBuilder) {
    TextView inputTextView = view.findViewById(R.id.tvPhotoEditorText);
    if (inputTextView != null && addedViews.contains(view) && !TextUtils.isEmpty(inputText)) {
      inputTextView.setText(inputText);
      if (styleBuilder != null)
        styleBuilder.applyStyle(inputTextView);
    
      parentView.updateViewLayout(view, view.getLayoutParams());
      int i = addedViews.indexOf(view);
      if (i > -1) addedViews.set(i, view);
    }
  }
  
  private void addViewToParent(View rootView, ViewType viewType) {
    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
      ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
    parentView.addView(rootView, params);
    addedViews.add(rootView);
    if (mOnPhotoEditorListener != null)
      mOnPhotoEditorListener.onAddViewListener(viewType, addedViews.size());
  }
  
  @NonNull
  private MultiTouchListener getMultiTouchListener() {
    MultiTouchListener multiTouchListener = new MultiTouchListener(
      deleteView,
      parentView,
      this.imageView,
      isTextPinchZoomable,
      mOnPhotoEditorListener);
    
    //multiTouchListener.setOnMultiTouchListener(this);
    
    return multiTouchListener;
  }
  
  private View getLayout(final ViewType viewType) {
    View rootView = null;
    switch (viewType) {
      case TEXT:
        rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_text, null);
        TextView txtText = rootView.findViewById(R.id.tvPhotoEditorText);
        if (txtText != null && mDefaultTextTypeface != null) {
          txtText.setGravity(Gravity.CENTER);
          if (mDefaultTextTypeface != null) {
            txtText.setTypeface(mDefaultTextTypeface);
          }
        }
        break;
      case IMAGE:
        rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_image, null);
        break;
    }
    
    return rootView;
  }
  
  public void setBrushDrawingMode(boolean brushDrawingMode) {
    if (brushDrawingView != null)
      brushDrawingView.setBrushDrawingMode(brushDrawingMode);
  }
  
  public Boolean getBrushDrawableMode() {
    return brushDrawingView != null && brushDrawingView.getBrushDrawingMode();
  }
  
  public void setBrushSize(float size) {
    if (brushDrawingView != null)
      brushDrawingView.setBrushSize(size);
  }
  
  public void setOpacity(@IntRange(from = 0, to = 100) int opacity) {
    if (brushDrawingView != null) {
      opacity = (int) ((opacity / 100.0) * 255.0);
      brushDrawingView.setOpacity(opacity);
    }
  }
  
  public void setBrushColor(@ColorInt int color) {
    if (brushDrawingView != null)
      brushDrawingView.setBrushColor(color);
  }
  
  public void setBrushEraserSize(float brushEraserSize) {
    if (brushDrawingView != null)
      brushDrawingView.setBrushEraserSize(brushEraserSize);
  }
  
  void setBrushEraserColor(@ColorInt int color) {
    if (brushDrawingView != null)
      brushDrawingView.setBrushEraserColor(color);
  }
  
  public float getEraserSize() {
    return brushDrawingView != null ? brushDrawingView.getEraserSize() : 0;
  }
  
  public float getBrushSize() {
    if (brushDrawingView != null)
      return brushDrawingView.getBrushSize();
    return 0;
  }
  
  public int getBrushColor() {
    if (brushDrawingView != null) {
      return brushDrawingView.getBrushColor();
    }
    return 0;
  }
  
  public void brushEraser() {
    if (brushDrawingView != null) {
      brushDrawingView.brushEraser();
    }
  }
  
  private void viewUndo(View removedView, ViewType viewType) {
    if (addedViews.size() > 0) {
      if (addedViews.contains(removedView)) {
        parentView.removeView(removedView);
        addedViews.remove(removedView);
        redoViews.add(removedView);
        if (mOnPhotoEditorListener != null) {
          mOnPhotoEditorListener.onRemoveViewListener(viewType, addedViews.size());
        }
      }
    }
  }
  
  public boolean undo() {
    if (addedViews.size() > 0) {
      View removeView = addedViews.get(addedViews.size() - 1);
      if (removeView instanceof BrushDrawingView) {
        return brushDrawingView != null && brushDrawingView.undo();
      } else {
        addedViews.remove(addedViews.size() - 1);
        parentView.removeView(removeView);
        redoViews.add(removeView);
      }
      if (mOnPhotoEditorListener != null) {
        Object viewTag = removeView.getTag();
        if (viewTag != null && viewTag instanceof ViewType) {
          mOnPhotoEditorListener.onRemoveViewListener(((ViewType) viewTag), addedViews.size());
        }
      }
    }
    return addedViews.size() != 0;
  }
  
  public boolean redo() {
    if (redoViews.size() > 0) {
      View redoView = redoViews.get(redoViews.size() - 1);
      if (redoView instanceof BrushDrawingView) {
        return brushDrawingView != null && brushDrawingView.redo();
      } else {
        redoViews.remove(redoViews.size() -1);
        parentView.addView(redoView);
        addedViews.add(redoView);
      }
      Object viewTag = redoView.getTag();
      if (mOnPhotoEditorListener != null && viewTag != null && viewTag instanceof ViewType) {
        mOnPhotoEditorListener.onAddViewListener(((ViewType) viewTag), addedViews.size());
      }
    }
    return redoViews.size() != 0;
  }
  
  private void clearBrushAllViews() {
    if (brushDrawingView != null)
      brushDrawingView.clearAll();
  }
  
  public void clearAllViews() {
    for (int i = 0; i < addedViews.size(); i++) {
      parentView.removeView(addedViews.get(i));
    }
    if (addedViews.contains(brushDrawingView)) {
      parentView.addView(brushDrawingView);
    }
    addedViews.clear();
    redoViews.clear();
    clearBrushAllViews();
  }
  
  @UiThread
  public void clearHelperBox() {
    for (int i = 0; i < parentView.getChildCount(); i++) {
      View childAt = parentView.getChildAt(i);
      FrameLayout frmBorder = childAt.findViewById(R.id.frmBorder);
      if (frmBorder != null) {
        frmBorder.setBackgroundResource(0);
      }
      ImageView imgClose = childAt.findViewById(R.id.imgPhotoEditorClose);
      if (imgClose != null) {
        imgClose.setVisibility(View.GONE);
      }
    }
  }
  
  public interface OnSaveListener {
    void onSuccess(@NonNull String imagePath);
    void onFailure(@NonNull Exception exception);
  }
  
  @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
  public void saveAsFile(@NonNull final String imagePath, @NonNull final OnSaveListener onSaveListener) {
    saveAsFile(imagePath, new SaveSettings.Builder().build(), onSaveListener);
  }
  
  @SuppressLint("StaticFieldLeak")
  @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
  public void saveAsFile(@NonNull final String imagePath,
                         @NonNull final SaveSettings saveSettings,
                         @NonNull final OnSaveListener onSaveListener) {
    
    parentView.saveFilter(new OnSaveBitmap() {
      
      @Override
      public void onBitmapReady(Bitmap saveBitmap) {
        new AsyncTask<String, String, Exception>() {
          
          @Override
          protected void onPreExecute() {
            super.onPreExecute();
            clearHelperBox();
            parentView.setDrawingCacheEnabled(false);
          }
          
          @SuppressLint("MissingPermission")
          @Override
          protected Exception doInBackground(String... strings) {
            File file = new File(imagePath);
            try {
              FileOutputStream out = new FileOutputStream(file, false);
              if (parentView != null) {
                parentView.setDrawingCacheEnabled(true);
                Bitmap drawingCache = saveSettings.isTransparencyEnabled()
                  ? BitmapUtil.removeTransparency(parentView.getDrawingCache())
                  : parentView.getDrawingCache();
                drawingCache.compress(saveSettings.getCompressFormat(), saveSettings.getCompressQuality(), out);
              }
              out.flush();
              out.close();
              return null;
            } catch (Exception e) {
              e.printStackTrace();
              return e;
            }
          }
          
          @Override
          protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            if (e == null) {
              // Clear all views if its enabled in save settings.
              if (saveSettings.isClearViewsEnabled()) clearAllViews();
              onSaveListener.onSuccess(imagePath);
            } else {
              onSaveListener.onFailure(e);
            }
          }
        
        }.execute();
      }
      
      @Override
      public void onFailure(Exception e) {
        onSaveListener.onFailure(e);
      }
    });
  }
  
  public void setOnPhotoEditorListener(@NonNull OnPhotoEditorListener onPhotoEditorListener) {
    this.mOnPhotoEditorListener = onPhotoEditorListener;
  }
  
  public boolean isCacheEmpty() {
    return addedViews.size() == 0 && redoViews.size() == 0;
  }
  
  @Override
  public void onViewAdd(BrushDrawingView brushDrawingView) {
    if (redoViews.size() > 0) {
      redoViews.remove(redoViews.size() - 1);
    }
    addedViews.add(brushDrawingView);
    if (mOnPhotoEditorListener != null) {
      mOnPhotoEditorListener.onAddViewListener(ViewType.BRUSH_DRAWING, addedViews.size());
    }
  }
  
  @Override
  public void onViewRemoved(BrushDrawingView brushDrawingView) {
    if (addedViews.size() > 0) {
      View removeView = addedViews.remove(addedViews.size() - 1);
      if (!(removeView instanceof BrushDrawingView)) {
        parentView.removeView(removeView);
      }
      redoViews.add(removeView);
    }
    if (mOnPhotoEditorListener != null) {
      mOnPhotoEditorListener.onRemoveViewListener(ViewType.BRUSH_DRAWING, addedViews.size());
    }
  }
  
  @Override
  public void onStartDrawing() {
    if (mOnPhotoEditorListener != null) {
      mOnPhotoEditorListener.onStartViewChangeListener(ViewType.BRUSH_DRAWING);
    }
  }
  
  @Override
  public void onStopDrawing() {
    if (mOnPhotoEditorListener != null) {
      mOnPhotoEditorListener.onStopViewChangeListener(ViewType.BRUSH_DRAWING);
    }
  }
  
  public static class Builder {
    
    private Context context;
    private PhotoEditorView parentView;
    private AppCompatImageView imageView;
    private View deleteView;
    private BrushDrawingView brushDrawingView;
    private Typeface textTypeface;
    private boolean isTextPinchZoomable = true;
    
    public Builder(Context context, PhotoEditorView photoEditorView) {
      this.context = context;
      parentView = photoEditorView;
      imageView = photoEditorView.getSource();
      brushDrawingView = photoEditorView.getBrushDrawingView();
    }
    
    Builder setDeleteView(View deleteView) {
      this.deleteView = deleteView;
      return this;
    }
    
    public Builder setDefaultTextTypeface(Typeface textTypeface) {
      this.textTypeface = textTypeface;
      return this;
    }
    
    public Builder setPinchTextScalable(boolean isTextPinchZoomable) {
      this.isTextPinchZoomable = isTextPinchZoomable;
      return this;
    }
    
    public PhotoEditor build() {
      return new PhotoEditor(this);
    }
  }
}
