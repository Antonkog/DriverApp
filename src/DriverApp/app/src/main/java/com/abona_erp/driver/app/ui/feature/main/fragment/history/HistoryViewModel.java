package com.abona_erp.driver.app.ui.feature.main.fragment.history;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.abona_erp.driver.app.data.entity.ChangeHistory;
import com.abona_erp.driver.app.data.repository.DriverRepository;

import java.util.ArrayList;
import java.util.List;

public class HistoryViewModel extends AndroidViewModel {
  private final String TAG = "HistoryViewModel";
  private DriverRepository _repo;
  private MutableLiveData<List<ChangeHistory>> filteredLogs = new MutableLiveData<>();
  private List<ChangeHistory> logsByTaskId = new ArrayList<>();
  private List<ChangeHistory> logsByOrderNum = new ArrayList<>();
  private LiveData<List<ChangeHistory>> allLogs;
  private boolean filterByTaskNotOrder = true;

  public HistoryViewModel(Application application) {
    super(application);
    _repo = new DriverRepository(application);
  }

  public void setHistoryLogs(){
    allLogs =  _repo.getChangeHistoryDao().getLogs();
  }

  public LiveData<List<ChangeHistory>> getHistoryLogs() {
    return allLogs;
  }

  public MutableLiveData<List<ChangeHistory>> getFilteredLogs() {
    return filteredLogs;
  }

  public void deleteChangeHistory() {
    _repo.deleteChangeHistory();
  }

  public void setHistoryLogsByOrderNumber() {
    filterByTaskNotOrder = false;
    filteredLogs.postValue(logsByOrderNum);
  }

  public void setLogsWithTaskId() {
    filterByTaskNotOrder = true;
    filteredLogs.postValue(logsByTaskId);
  }

  public void refreshLogs(List<ChangeHistory> logItems, int currentTaskId, int currentOrderNum) {

    filterByOrderNum(logItems, currentOrderNum);

    filterByTaskId(logItems, currentTaskId);

    if (filterByTaskNotOrder)
      filteredLogs.postValue(logsByTaskId);
    else
      filteredLogs.postValue(logsByOrderNum);
  }

  private void filterByTaskId(List<ChangeHistory> logItems, int currentTaskId) {
    if(logItems!= null && !logItems.isEmpty()){
      logsByTaskId.clear();
      for (int i = 0; i <logItems.size() ; i++) {
        if(logItems.get(i).getTaskId() == currentTaskId)
          logsByTaskId.add(logItems.get(i));
      }
    }
  }

  private void filterByOrderNum(List<ChangeHistory> logItems, int currentOrderNum) {
    if(logItems!= null && !logItems.isEmpty()){
      logsByOrderNum.clear();
      for (int i = 0; i <logItems.size() ; i++) {
        if(logItems.get(i).getOrderNumber() == currentOrderNum)
          logsByOrderNum.add(logItems.get(i));
      }
    }
  }
}
