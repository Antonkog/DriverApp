package com.abona_erp.driver.app.ui.feature.main.fragment.about;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.ui.event.BackEvent;
import com.abona_erp.driver.app.ui.widget.AsapTextView;

public class SoftwareAboutFragment extends Fragment {
  
  private static final String TAG = SoftwareAboutFragment.class.getSimpleName();
  
  private AppCompatImageButton mBtnBack;
  private AsapTextView         mVersionName;
  
  public SoftwareAboutFragment() {
    // Required empty public constructor.
  }
  
  public static SoftwareAboutFragment newInstance() {
    return new SoftwareAboutFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.activity_software_info_layout, container, false);
    initComponents(root);
    return root;
  }
  
  @Override
  public void onResume() {
    super.onResume();
  }
  
  private void initComponents(@NonNull View root) {
    mBtnBack = (AppCompatImageButton)root.findViewById(R.id.btn_software_about_back);
    mBtnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        App.eventBus.post(new BackEvent());
      }
    });
    
    mVersionName = (AsapTextView)root.findViewById(R.id.tv_version_name);
    mVersionName.setText("ver " + getVersionName());
  }
  
  private String getVersionName() {
    try {
      PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
      return pInfo.versionName;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return "UNKNOWN";
  }
}
