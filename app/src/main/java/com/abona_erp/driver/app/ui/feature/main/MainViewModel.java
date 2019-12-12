package com.abona_erp.driver.app.ui.feature.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
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
    
    //mAllDeviceProfiles = mRepository.getAllDeviceProfiles();
  }

  LiveData<Integer> getNotReadNotificationCount() {
    return mNotReadNotificationCount;
  }
  
  LiveData<Integer> getRowCount() {
    return mRowCount;
  }

  LiveData<List<LastActivity>> getAllLastActivityItems() {
    return mAllLastActivityItems;
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

  void insert(LastActivity lastActivity) {
    mRepository.insert(lastActivity);
  }

  void update(Notify notify) {
    mRepository.update(notify);
  }
  
  Single<Notify> getNotifyByMandantTaskId(int mandantId, int taskId) {
    return mRepository.getNotifyByMandantTaskId(mandantId, taskId);
  }
  
  // -----------------------------------------------------------------------------------------------
  // DEVICE PROFILE:
  void insert(DeviceProfile deviceProfile) {
    mRepository.insert(deviceProfile);
  }
  
  void update(DeviceProfile deviceProfile) {
    mRepository.update(deviceProfile);
  }
  
  //List<DeviceProfile> getAllDeviceProfiles() {
  //  return mAllDeviceProfiles;
  //}
  // DEVICE PROFILE:
  // -----------------------------------------------------------------------------------------------
  
  // -----------------------------------------------------------------------------------------------
  // LAST ACTIVITY:
  void delete(LastActivity lastActivity) {
    mRepository.delete(lastActivity);
  }
  // LAST ACTIVITY:
  // -----------------------------------------------------------------------------------------------
}
