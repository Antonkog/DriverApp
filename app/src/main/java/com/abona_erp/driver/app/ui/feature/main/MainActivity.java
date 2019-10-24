package com.abona_erp.driver.app.ui.feature.main;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.abona_erp.driver.app.R;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.repository.NotifyRepository;
import com.abona_erp.driver.app.ui.base.BaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends BaseActivity {
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  
    TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(this,
      getSupportFragmentManager());
  
    ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
    viewPager.setAdapter(tabsPagerAdapter);
  
    TabLayout tabs = findViewById(R.id.tabLayout);
    tabs.setupWithViewPager(viewPager);
  
    FirebaseInstanceId.getInstance().getInstanceId()
      .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
        @Override
        public void onComplete(@NonNull Task<InstanceIdResult> task) {
          if (!task.isSuccessful()) {
            Log.d("TEST", "Firebase getInstanceId failed " + task.getException());
            return;
          }
        
          // Get new Instance ID token:
          String token = task.getResult().getToken();
          Log.d("TEST","Firebase registration Token=" + token);
        }
      });
  }
}
