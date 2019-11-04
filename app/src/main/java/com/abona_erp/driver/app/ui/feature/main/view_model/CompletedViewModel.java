package com.abona_erp.driver.app.ui.feature.main.view_model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.repository.DriverRepository;

import java.util.List;

public class CompletedViewModel extends AndroidViewModel {

  private DriverRepository mRepository;

  private LiveData<List<Notify>> mAllCompletedNotifications;

  public CompletedViewModel(Application application) {
    super(application);
    mRepository = new DriverRepository(application);
    mAllCompletedNotifications = mRepository.getAllCompletedNotifications();
  }

  public LiveData<List<Notify>> getAllCompletedNotifications() {
    return mAllCompletedNotifications;
  }

  public void update(Notify notify) {
    mRepository.update(notify);
  }
}
