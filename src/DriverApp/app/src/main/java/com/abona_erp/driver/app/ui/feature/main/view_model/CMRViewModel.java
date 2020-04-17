package com.abona_erp.driver.app.ui.feature.main.view_model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.repository.DriverRepository;

import java.util.List;

public class CMRViewModel extends AndroidViewModel {

  private DriverRepository mRepository;

  private LiveData<List<Notify>> mAllCMRNotifications;

  public CMRViewModel(Application application) {
    super(application);
    mRepository = new DriverRepository(application);
    mAllCMRNotifications = mRepository.getAllCMRNotifications();
  }

  public LiveData<List<Notify>> getAllCMRNotifications() {
    return mAllCMRNotifications;
  }

  public void update(Notify notify) {
    mRepository.update(notify);
  }
}
