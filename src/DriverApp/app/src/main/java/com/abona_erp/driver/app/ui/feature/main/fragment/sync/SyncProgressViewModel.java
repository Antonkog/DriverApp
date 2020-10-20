package com.abona_erp.driver.app.ui.feature.main.fragment.sync;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.entity.OfflineDelayReasonEntity;
import com.abona_erp.driver.app.data.repository.DriverRepository;

import java.util.List;

public class SyncProgressViewModel extends AndroidViewModel {
  private final String TAG = "HistoryViewModel";
  private DriverRepository _repo;
  private LiveData<List<OfflineConfirmation>> offlineConfirmations;
  private LiveData<List<OfflineDelayReasonEntity>> offlineDelays;


  LiveData<List<OfflineConfirmation>> getAllOfflineConfirmations() {
    return offlineConfirmations;
  }


  LiveData<List<OfflineDelayReasonEntity>> getAllDelayConfirmations() {
    return offlineDelays;
  }


  public SyncProgressViewModel(Application application) {
    super(application);
    _repo = new DriverRepository(application);
  }

  void setConfirmations()
  {
    offlineConfirmations = _repo.getAllLiveDataConfirmations();
    offlineDelays = _repo.getOfflineDelayReasons();
  }
}
