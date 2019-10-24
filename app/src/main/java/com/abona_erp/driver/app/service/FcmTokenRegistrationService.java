package com.abona_erp.driver.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class FcmTokenRegistrationService extends IntentService {
  
  private static final String TAG =
    FcmTokenRegistrationService.class.getSimpleName();
  
  public FcmTokenRegistrationService() {
    super("FcmTokenRegistrationService");
  }
  
  @Override
  protected void onHandleIntent(Intent intent) {
  
  
  }
}
