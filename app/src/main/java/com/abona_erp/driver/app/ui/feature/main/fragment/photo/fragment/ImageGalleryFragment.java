package com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.abona_erp.driver.app.R;


public class ImageGalleryFragment extends Fragment {
  
  private static final String TAG = ImageGalleryFragment.class.getSimpleName();
  
  private static final int READ_STORAGE_PERMISSIONS_REQUEST = 1;
  
  private GridView mGalleryView;
  
  public ImageGalleryFragment() {
    // Required empty public constructor.
  }
  
  public static ImageGalleryFragment newInstance() {
    return new ImageGalleryFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.sub_fragment_image_gallery_view, container, false);
    initComponents(root);
    return root;
  }
  
  @Override
  public void onResume() {
    super.onResume();
  }
  
  private void setGalleryAdapter() {
  }
  
  @TargetApi(Build.VERSION_CODES.M)
  public void getPermissionToReadStorage() {
    if (ContextCompat.checkSelfPermission(getActivity(),
      Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
        // TODO: make some action here.
      }
      requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
        READ_STORAGE_PERMISSIONS_REQUEST);
    } else {
      setGalleryAdapter();
    }
  }
  
  @Override
  public void onRequestPermissionsResult(
    final int requestCode,
    @NonNull final String[] permissions,
    @NonNull final int[] grantResults
  ) {
    if (requestCode == READ_STORAGE_PERMISSIONS_REQUEST) {
      if (grantResults.length == 1
        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        setGalleryAdapter();
      } else {
        getActivity().finish();
      }
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }
  
  private void initComponents(@NonNull View root) {
    
    mGalleryView = (GridView)root.findViewById(R.id.gv_gallery);
    getPermissionToReadStorage();
  }
}
