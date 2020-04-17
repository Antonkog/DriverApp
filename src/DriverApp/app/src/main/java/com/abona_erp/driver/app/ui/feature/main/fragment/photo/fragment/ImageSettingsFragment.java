package com.abona_erp.driver.app.ui.feature.main.fragment.photo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.abona_erp.driver.app.R;

public class ImageSettingsFragment extends Fragment {
  
  private static final String TAG = ImageSettingsFragment.class.getSimpleName();
  
  public ImageSettingsFragment() {
    // Required empty public constructor.
  }
  
  public static ImageSettingsFragment newInstance() {
    return new ImageSettingsFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.sub_fragment_image_settings_view, container, false);
    initComponents(root);
    return root;
  }
  
  @Override
  public void onResume() {
    super.onResume();
  }
  
  private void initComponents(@NonNull View root) {
  }
}
