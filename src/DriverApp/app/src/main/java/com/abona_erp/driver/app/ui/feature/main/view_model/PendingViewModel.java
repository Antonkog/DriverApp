package com.abona_erp.driver.app.ui.feature.main.view_model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.repository.DriverRepository;
import com.abona_erp.driver.app.util.AppUtils;

import java.util.List;

public class PendingViewModel extends AndroidViewModel {

  private DriverRepository mRepository;

  private LiveData<List<Notify>> mAllPendingNotifications;

  public PendingViewModel(Application application) {
    super(application);
    mRepository = new DriverRepository(application);
    mAllPendingNotifications = mRepository.getAllPendingNotifications();
  }

  public LiveData<List<Notify>> getAllPendingNotifications() {
    return mAllPendingNotifications;
  }

  public void update(Notify notify) {
    mRepository.update(notify);
  }
}
