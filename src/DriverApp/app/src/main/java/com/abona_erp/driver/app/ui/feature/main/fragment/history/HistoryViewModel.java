package com.abona_erp.driver.app.ui.feature.main.fragment.history;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.abona_erp.driver.app.data.entity.ChangeHistory;
import com.abona_erp.driver.app.data.entity.LogItem;
import com.abona_erp.driver.app.data.repository.DriverRepository;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel {
  private final String TAG = "HistoryViewModel";
  private DriverRepository _repo;
  private LiveData<List<ChangeHistory>> mAllLogs;

  public HistoryViewModel(Application application) {
    super(application);
    _repo = new DriverRepository(application);
    mAllLogs =  _repo.getChangeHistoryDao().getLogs();
  }
  
  public LiveData<List<ChangeHistory>> getHistoryLogs() {
    return mAllLogs;
  }

  
  public void deleteChangeHistory() {
    _repo.deleteChangeHistory();
  }
}
