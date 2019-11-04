package com.abona_erp.driver.app.ui.feature.main.view_model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.repository.DriverRepository;

import java.util.List;

public class RunningViewModel extends AndroidViewModel {

  private DriverRepository mRepository;

  private LiveData<List<Notify>> mAllRunningNotifications;

  public RunningViewModel(Application application) {
    super(application);
    mRepository = new DriverRepository(application);
    mAllRunningNotifications = mRepository.getAllRunningNotifications();
  }

  public LiveData<List<Notify>> getAllRunningNotifications() {
    return mAllRunningNotifications;
  }

  public void update(Notify notify) {
    mRepository.update(notify);
  }
}
