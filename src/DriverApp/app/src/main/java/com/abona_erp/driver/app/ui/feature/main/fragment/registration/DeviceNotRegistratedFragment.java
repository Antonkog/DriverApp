package com.abona_erp.driver.app.ui.feature.main.fragment.registration;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.RegistrationErrorEvent;
import com.abona_erp.driver.app.ui.event.RegistrationFinishedEvent;
import com.abona_erp.driver.app.ui.event.RegistrationStartEvent;
import com.abona_erp.driver.app.util.TextSecurePreferences;
import com.abona_erp.driver.core.base.ThreadUtils;

import org.greenrobot.eventbus.Subscribe;

public class DeviceNotRegistratedFragment extends Fragment {
  
  private static final String TAG = DeviceNotRegistratedFragment.class.getSimpleName();
  
  private ProgressDialog mProgressDialog;
  
  public DeviceNotRegistratedFragment() {
    // Required empty public constructor.
  }
  
  public static DeviceNotRegistratedFragment newInstance() {
    return new DeviceNotRegistratedFragment();
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.activity_not_registrated, container, false);
    
    mProgressDialog = new ProgressDialog(getContext());
    
    return root;
  }
  
  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    
    mProgressDialog.setMax(100);
    mProgressDialog.setMessage("Device Registration started...");
    mProgressDialog.setTitle("ABONA Driver App");
    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    /*mProgressDialog.setCancelable(false);*/
    mProgressDialog.show();
    
    mProgressDialog.setProgress(10);
  
    TextSecurePreferences.setStopService(false);
    TextSecurePreferences.setRegistrationStarted(true);
  }
  
  @Subscribe
  public void onMessageEvent(RegistrationStartEvent event) {
    if (mProgressDialog != null) {
      if (mProgressDialog.isShowing()) {
        mProgressDialog.setMessage("Device Registration started...");
      }
    }
  }
  
  @Subscribe
  public void onMessageEvent(RegistrationErrorEvent event) {
    if (mProgressDialog != null) {
      if (mProgressDialog.isShowing()) {
        mProgressDialog.setMessage("Error on REST-API...");
      }
    }
  }
  
  @Subscribe
  public void onMessageEvent(RegistrationFinishedEvent event) {
    if (mProgressDialog != null) {
      if (mProgressDialog.isShowing()) {
        mProgressDialog.setMessage("Device Registration Successful!");
      }
    }
  }
  
  @Override
  public void onStart() {
    Log.d(TAG, "onStart()");
    App.eventBus.register(this);
    super.onStart();
  }
  
  @Override
  public void onStop() {
    super.onStop();
    Log.d(TAG, "onStop()");
    App.eventBus.unregister(this);
  }
  
  @Override
  public void onDestroyView() {
    super.onDestroyView();
  
    mProgressDialog.setProgress(100);
    ThreadUtils.postOnUiThread(() -> mProgressDialog.dismiss());
  }
}
