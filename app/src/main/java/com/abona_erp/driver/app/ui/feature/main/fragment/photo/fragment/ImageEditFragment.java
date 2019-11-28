package com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.abona_erp.driver.app.R;
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
    mPhotoEditorView.getSource().setImageBitmap(bitmap);
  }
  
  @Override
  public void onResume() {
    super.onResume();
  }
  
  private void initComponents(@NonNull View root) {
    mPhotoEditorView = (PhotoEditorView)root.findViewById(R.id.photo_editor_view);
  }
}
