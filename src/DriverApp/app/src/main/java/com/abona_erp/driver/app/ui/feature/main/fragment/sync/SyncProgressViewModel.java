package com.abona_erp.driver.app.ui.feature.main.fragment.sync;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.abona_erp.driver.app.data.entity.ChangeHistory;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.repository.DriverRepository;
import com.abona_erp.driver.app.util.AppUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class SyncProgressViewModel extends AndroidViewModel {
  private final String TAG = "HistoryViewModel";
  private DriverRepository _repo;
  private LiveData<List<OfflineConfirmation>> mAllOfflineConfirmations;


  LiveData<List<OfflineConfirmation>> getAllOfflineConfirmations() {
    return mAllOfflineConfirmations;
  }


  public SyncProgressViewModel(Application application) {
    super(application);
    _repo = new DriverRepository(application);
  }

  void setConfirmations(){
    mAllOfflineConfirmations = _repo.getAllLiveDataConfirmations();
  }
}
