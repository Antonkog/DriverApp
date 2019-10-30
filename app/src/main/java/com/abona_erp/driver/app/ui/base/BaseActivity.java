package com.abona_erp.driver.app.ui.base;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import com.abona_erp.driver.app.R;

public abstract class BaseActivity extends AppCompatActivity {
  
  public ProgressBar mProgressBar;
  
  public NestedScrollView mTaskDetailView;
  public AppCompatImageButton mTaskDetailViewClose;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    mTaskDetailView = (NestedScrollView) findViewById(R.id.task_detail_view);
  }
  
  @Override
  public void setContentView(int layoutResID) {
  
    ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
    FrameLayout frameLayout = constraintLayout.findViewById(R.id.activity_content);
    mProgressBar = constraintLayout.findViewById(R.id.progress_bar);
    
    getLayoutInflater().inflate(layoutResID, frameLayout, true);
    super.setContentView(constraintLayout);
  }
  
  public void showProgressBar(boolean visibility) {
    mProgressBar.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
  }
  
  public void registerStartActivity() {
    ((AppCompatButton) findViewById(R.id.task_detail_view_start_activity))
      .setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          hideTaskDetailView();
          
        }
      });
  }
  
  public void registerCloseTaskDetailView() {
    ((AppCompatImageButton) findViewById(R.id.task_detail_view_close))
      .setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          hideTaskDetailView();
        }
      });
  }
  
  public void showTaskDetailView() {
    if (mTaskDetailView != null) {
      mTaskDetailView.setVisibility(View.VISIBLE);
    } else {
      mTaskDetailView = (NestedScrollView) findViewById(R.id.task_detail_view);
      mTaskDetailView.setVisibility(View.VISIBLE);
    }
  }
  
  public void hideTaskDetailView() {
    if (mTaskDetailView != null) {
      mTaskDetailView.setVisibility(View.GONE);
    } else {
      mTaskDetailView = (NestedScrollView) findViewById(R.id.task_detail_view);
      mTaskDetailView.setVisibility(View.GONE);
    }
  }
  
  public void showMapView() {
    ((RelativeLayout) findViewById(R.id.map_layout))
      .setVisibility(View.VISIBLE);
  }
  
  public void hideMapView() {
    ((RelativeLayout) findViewById(R.id.map_layout))
      .setVisibility(View.GONE);
  }
}
