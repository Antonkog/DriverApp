package com.abona_erp.driver.app.ui.feature.main.fragment.protocol;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.abona_erp.driver.app.data.entity.LogItem;
import com.abona_erp.driver.app.data.repository.DriverRepository;

import java.util.List;

public class ProtocolViewModel extends AndroidViewModel {
  
  private DriverRepository _repo;
  private LiveData<List<LogItem>> mAllLogs;
  
  public ProtocolViewModel(Application application) {
    super(application);
    _repo = new DriverRepository(application);
    //mAllLogs = _repo.getLogsDAO().getProtocolLogs();
    mAllLogs = _repo.getLogsDAO().getLogs();
  }
  
  public LiveData<List<LogItem>> getAllLogs() {
    return mAllLogs;
  }
  
  public void insert(LogItem item) {
    _repo.insert(item);
  }

}
