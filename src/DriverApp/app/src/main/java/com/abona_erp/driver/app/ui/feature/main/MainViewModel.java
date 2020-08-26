package com.abona_erp.driver.app.ui.feature.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.abona_erp.driver.app.data.converters.LogLevel;
import com.abona_erp.driver.app.data.converters.LogType;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.LogItem;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.repository.DriverRepository;

import java.util.Date;
import java.util.List;

import io.reactivex.Flowable;
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
  
  Single<List<Notify>> getAllTasksByMandantAndOrderNo(int mandantID, int orderNo) {
    return mRepository.getAllTasksByOrderNo(mandantID, orderNo);
  }


  public void addLog(LogItem item){
    insert(item);
  }
  public void addLog(String message, LogType type, LogLevel level, String title){
//    addLog(message, type, level, title, 0);
  }

  private void addLog(String message, LogType type, LogLevel level, String title, int taskId) {
    LogItem item = new LogItem();
    item.setLevel(level);
    item.setType(type);
    item.setTitle(title);
    item.setMessage(message);
    item.setCreatedAt(new Date());
    item.setTaskId(taskId);
    insert(item);
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

  /**
   * Get the DeviceProfile
   *
   * @return a {@link Flowable} DeviceProfile.
   */
  public Flowable<DeviceProfile> getDeviceProfile() {
    return  mRepository.getmDeviceProfileDAO().getDeviceProfile();
  }

  // -----------------------------------------------------------------------------------------------
  // DEVICE PROFILE:
  void insert(DeviceProfile deviceProfile) {
    mRepository.insert(deviceProfile);
  }
  
  void update(DeviceProfile deviceProfile) {
    mRepository.update(deviceProfile);
  }

  public void deleteOldTables(){
    mRepository.deleteAllNotify();
    mRepository.deleteAllLastActivities();
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
