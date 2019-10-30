package com.abona_erp.driver.app.ui.feature.main.steps;

import android.view.LayoutInflater;
import android.view.View;

import com.abona_erp.driver.app.R;

import ernestoyaquello.com.verticalstepperform.Step;

public class ActivityStep extends Step<String> {
  
  public ActivityStep(String title) {
    this(title, "");
  }
  
  public ActivityStep(String title, String subtitle) {
    super(title, subtitle);
  }
  
  @Override
  public String getStepData() {
    return "";
  }
  
  @Override
  public String getStepDataAsHumanReadableString() {
    return null;
  }
  
  @Override
  public void restoreStepData(String data) {
  
  }
  
  @Override
  protected IsDataValid isStepDataValid(String stepData) {
    return new IsDataValid(true);
  }
  
  @Override
  protected View createStepContentLayout() {
  
    LayoutInflater inflater = LayoutInflater.from(getContext());
    View view = inflater.inflate(R.layout.step_activity, null, false);
    
    return view;
  }
  
  @Override
  protected void onStepOpened(boolean animated) {
  
  }
  
  @Override
  protected void onStepClosed(boolean animated) {
  
  }
  
  @Override
  protected void onStepMarkedAsCompleted(boolean animated) {
  
  }
  
  @Override
  protected void onStepMarkedAsUncompleted(boolean animated) {
  
  }
}
