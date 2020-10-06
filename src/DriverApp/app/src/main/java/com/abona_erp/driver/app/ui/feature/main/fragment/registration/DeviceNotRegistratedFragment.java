package com.abona_erp.driver.app.ui.feature.main.fragment.registration;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.logging.Log;
import com.abona_erp.driver.app.ui.event.RegistrationEvent;
import com.abona_erp.driver.app.util.TextSecurePreferences;

import org.greenrobot.eventbus.Subscribe;

public class DeviceNotRegistratedFragment extends Fragment {
  
  private static final String TAG = DeviceNotRegistratedFragment.class.getSimpleName();
  
  private ProgressDialog mProgressDialog;
  private Handler h = new Handler();
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
    mProgressDialog = new ProgressDialog(getActivity());
    return root;
  }
  
  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    showProgressDialog(getContext().getString(R.string.registration_started));
    TextSecurePreferences.setStopService(false);
    TextSecurePreferences.setRegistrationStarted(true);
  }
  
  @Subscribe
  public void onMessageEvent(RegistrationEvent event) {
    switch (event.getState()){
      case FINISHED:
        showProgressDialog(getContext().getString(R.string.registration_success));
        break;
      case ERROR:
        showProgressDialog(getContext().getString(R.string.registration_rest_error));
        break;
      case STARTED:
        showProgressDialog(getContext().getString(R.string.registration_started));
        break;
    }
  }

  private void showProgressDialog(String message){
    h.post(() -> {
      if(mProgressDialog.isShowing()) mProgressDialog.dismiss();
      if(this.isAdded()){
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(10);
        mProgressDialog.setMessage(message);
        mProgressDialog.setTitle("ABONA Driver App");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
      }
    });
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
    if(mProgressDialog!=null)
      mProgressDialog.dismiss();
    h.post(() -> mProgressDialog.dismiss());
  }
}
