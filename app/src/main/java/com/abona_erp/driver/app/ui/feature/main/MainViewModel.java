package com.abona_erp.driver.app.ui.feature.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.repository.DriverRepository;
import com.abona_erp.driver.app.util.AppUtils;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

  private DriverRepository mRepository;

  private LiveData<Integer> mNotReadNotificationCount;
  private LiveData<List<LastActivity>> mAllLastActivityItems;

  private LiveData<List<Notify>> mAllPendingNotifications;
  private LiveData<List<Notify>> mAllRunningNotifications;
  private LiveData<List<Notify>> mAllCMRNotifications;
  private LiveData<List<Notify>> mAllCompletedNotifications;

  public MainViewModel(Application application) {
    super(application);
    mRepository = new DriverRepository(application);
    mAllLastActivityItems = mRepository.getAllLastActivityItems();
    mNotReadNotificationCount = mRepository.getNotReadNotificationCount();

    mAllPendingNotifications = mRepository.getAllPendingNotifications();
    mAllRunningNotifications = mRepository.getAllRunningNotifications();
    mAllCMRNotifications = mRepository.getAllCMRNotifications();
    mAllCompletedNotifications = mRepository.getAllCompletedNotifications();
  }

  LiveData<Integer> getNotReadNotificationCount() {
    return mNotReadNotificationCount;
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
}
