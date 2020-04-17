package com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.photolib.PhotoEditorView;

public class ImageEditFragment extends Fragment {
  
  private static final String TAG = ImageEditFragment.class.getSimpleName();
  
  private static ImageEditFragment sINSTANCE = null;
  private PhotoEditorView mPhotoEditorView;
  
  public ImageEditFragment() {
    // Required empty public constructor.
  }
  
  public static ImageEditFragment newInstance() {
    if (sINSTANCE == null) {
      sINSTANCE = new ImageEditFragment();
    }
    return sINSTANCE;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.sub_fragment_image_edit_view, container, false);
    initComponents(root);
    return root;
  }
  
  public void setBitmap(Bitmap bitmap) {
    try {
      if (getContext() != null && bitmap == null) {
        mPhotoEditorView.getSource().setImageBitmap(drawableToBitmap(getContext().getResources().getDrawable(R.drawable.no_image_rect)));
      } else {
        mPhotoEditorView.getSource().setImageBitmap(bitmap);
      }
    } catch (NullPointerException e) {
      Log.e(TAG, e.getMessage());
    }
  }
  
  @Override
  public void onResume() {
    super.onResume();
  }
  
  private void initComponents(@NonNull View root) {
    mPhotoEditorView = (PhotoEditorView)root.findViewById(R.id.photo_editor_view);
  }
  
  public static Bitmap drawableToBitmap (Drawable drawable) {
    Bitmap bitmap = null;
    
    if (drawable instanceof BitmapDrawable) {
      BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
      if(bitmapDrawable.getBitmap() != null) {
        return bitmapDrawable.getBitmap();
      }
    }
    
    if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
      bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
    } else {
      bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    }
    
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);
    return bitmap;
  }
}
