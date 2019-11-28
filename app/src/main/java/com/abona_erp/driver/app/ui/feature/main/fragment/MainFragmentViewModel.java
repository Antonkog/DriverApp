package com.abona_erp.driver.app.ui.feature.main.fragment;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.repository.DriverRepository;

import java.util.List;

import io.reactivex.Single;

public class MainFragmentViewModel extends AndroidViewModel {
  
  private DriverRepository mRepository;
  
  private LiveData<List<Notify>> mAllPendingNotifications;
  private LiveData<List<Notify>> mAllRunningNotifications;
  private LiveData<List<Notify>> mAllCMRNotifications;
  private LiveData<List<Notify>> mAllCompletedNotifications;
  
  private LiveData<Integer> mRowCount;
  
  public MainFragmentViewModel(Application application) {
    super(application);
    mRepository = new DriverRepository(application);
    mAllPendingNotifications = mRepository.getAllPendingNotifications();
    mAllRunningNotifications = mRepository.getAllRunningNotifications();
    mAllCMRNotifications = mRepository.getAllCMRNotifications();
    mAllCompletedNotifications = mRepository.getAllCompletedNotifications();
    
    mRowCount = mRepository.getRowCount();
  }
  
  LiveData<List<Notify>> getAllPendingNotifications() {
    return mAllPendingNotifications;
  }
  
  LiveData<List<Notify>> getAllRunningNotifications() {
    return mAllRunningNotifications;
  }
  
  LiveData<List<Notify>> getAllCMRNotifications() {
    return mAllCMRNotifications;
  }
  
  LiveData<List<Notify>> getAllCompletedNotifications() {
    return mAllCompletedNotifications;
  }
  
  LiveData<Integer> getRowCount() {
    return mRowCount;
  }
  
  public Single<Notify> getNotifyById(int id) {
    return mRepository.getNotifyById(id);
  }
  
  public void update(Notify notify) {
    mRepository.update(notify);
  }
  
  void delete(Notify notify) {
    mRepository.delete(notify);
  }
  
  void insert(LastActivity lastActivity) {
    mRepository.insert(lastActivity);
  }
}
