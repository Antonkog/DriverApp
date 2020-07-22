package com.abona_erp.driver.app.ui.feature.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.LogItem;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.repository.DriverRepository;

import java.util.List;

import io.reactivex.Single;

public class MainViewModel extends AndroidViewModel {

  private DriverRepository mRepository;

  private LiveData<Integer> mNotReadNotificationCount;
  private LiveData<Integer> mRowCount;
  private LiveData<List<LastActivity>> mAllLastActivityItems;

  private LiveData<List<Notify>> mAllPendingNotifications;
  private LiveData<List<Notify>> mAllRunningNotifications;
  private LiveData<List<Notify>> mAllCMRNotifications;
  private LiveData<List<Notify>> mAllCompletedNotifications;
  private LiveData<List<Notify>> mAllTasksByMandantAndOrderNo;
  
  private LiveData<List<OfflineConfirmation>> mAllOfflineConfirmations;
  
  //private List<DeviceProfile> mAllDeviceProfiles;

  public MainViewModel(Application application) {
    super(application);
    mRepository = new DriverRepository(application);
    mAllLastActivityItems = mRepository.getAllLastActivityItems();
    mNotReadNotificationCount = mRepository.getNotReadNotificationCount();
    mRowCount = mRepository.getRowCount();

    mAllPendingNotifications = mRepository.getAllPendingNotifications();
    mAllRunningNotifications = mRepository.getAllRunningNotifications();
    mAllCMRNotifications = mRepository.getAllCMRNotifications();
    mAllCompletedNotifications = mRepository.getAllCompletedNotifications();
    
    mAllOfflineConfirmations = mRepository.getAllLiveDataConfirmations();
    
    //mAllDeviceProfiles = mRepository.getAllDeviceProfiles();
  }
  
  LiveData<List<OfflineConfirmation>> getAllOfflineConfirmations() {
    return mAllOfflineConfirmations;
  }

  LiveData<Integer> getNotReadNotificationCount() {
    return mNotReadNotificationCount;
}


  LiveData<List<Notify>> getAllCompletedNotifications() {
    return mAllCompletedNotifications;
  }
  
  Single<List<Notify>> getAllTasksByMandantAndOrderNo(int mandantID, int orderNo) {
    return mRepository.getAllTasksByOrderNo(mandantID, orderNo);
  }
  Single<Notify> getNotifyByMandantTaskId(int mandantId, int taskId) {
    return mRepository.getNotifyByMandantTaskId(mandantId, taskId);
  }

  Single<Notify> getNotifyByTaskId(int taskId) {
    return mRepository.getNotifyByTaskId(taskId);
  }

  Single<LastActivity> getLastActivityByTaskClientId(int taskId, int clientId) {
    return mRepository.getLastActivityByTaskClientId(taskId, clientId);
  }

  
  void insert(LogItem item) {
    mRepository.insert(item);
  }
  
  void insert(Notify notify) {
    mRepository.insert(notify);
  }

  void insert(LastActivity lastActivity) {
    mRepository.insert(lastActivity);
  }

  void delete(LastActivity lastActivity) {
    mRepository.delete(lastActivity);
  }

  void update(Notify notify) {
    mRepository.update(notify);
  }
  
  void delete(Notify notify) {
    mRepository.delete(notify);
  }
  
  Single<Notify> getNotifyByMandantTaskId(int mandantId, int taskId) {
    return mRepository.getNotifyByMandantTaskId(mandantId, taskId);
  }

  public Single<Notify> getNotifyByTaskId(int taskId) {
      return mRepository.getNotifyByTaskId(taskId);
  }

  Single<LastActivity> getLastActivityByTaskClientId(int taskId, int clientId) {
    return mRepository.getLastActivityByTaskClientId(taskId, clientId);
  }
  
  // -----------------------------------------------------------------------------------------------
  // DEVICE PROFILE:

  void insert(DeviceProfile deviceProfile) {
    mRepository.insert(deviceProfile);
  }
  
  void update(DeviceProfile deviceProfile) {
    mRepository.update(deviceProfile);
  }

}
