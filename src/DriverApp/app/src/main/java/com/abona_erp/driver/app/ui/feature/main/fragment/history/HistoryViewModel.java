package com.abona_erp.driver.app.ui.feature.main.fragment.history;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.abona_erp.driver.app.data.entity.LogItem;
import com.abona_erp.driver.app.data.repository.DriverRepository;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel {

  private DriverRepository _repo;
  private LiveData<List<LogItem>> mAllLogs;

  public HistoryViewModel(Application application) {
    super(application);
    _repo = new DriverRepository(application);
    mAllLogs =  _repo.getLogsDAO().getLogs();
  }
  
  public LiveData<List<LogItem>> getHistoryLogs() {
    return mAllLogs;
  }

  
  public void deleteAllLogs() {
    _repo.deleteAllLogs();
  }
}
