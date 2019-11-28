package com.abona_erp.driver.app.data.repository;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.dao.DeviceProfileDAO;
import com.abona_erp.driver.app.data.dao.LastActivityDao;
import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.util.AppUtils;
import com.abona_erp.driver.core.base.ContextUtils;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class DriverRepository {

  private NotifyDao mNotifyDao;
  private LiveData<List<Notify>> mAllPendingNotifications;
  private LiveData<List<Notify>> mAllRunningNotifications;
  private LiveData<List<Notify>> mAllCMRNotifications;
  private LiveData<List<Notify>> mAllCompletedNotifications;
  private LiveData<Integer> mNotReadNotificationCount;
  private LiveData<Integer> mRowCount;

  private static LastActivityDao mLastActivityDao;
  private LiveData<List<LastActivity>> mAllLastActivityItems;
  
  private DeviceProfileDAO mDeviceProfileDAO;
  private LiveData<List<DeviceProfile>> mAllDeviceProfiles;
  
  public DriverRepository(Application application) {
    DriverDatabase db = DriverDatabase.getDatabase();
    mNotifyDao = db.notifyDao();
    mAllPendingNotifications = mNotifyDao.getAllPendingNotifications();
    mAllRunningNotifications = mNotifyDao.getAllRunningNotifications();
    mAllCMRNotifications = mNotifyDao.getAllCMRNotifications();
    mAllCompletedNotifications = mNotifyDao.getAllCompletedNotifications();
    mNotReadNotificationCount = mNotifyDao.getNotReadNotificationCount();
    mRowCount = mNotifyDao.getRowCount();

    mLastActivityDao = db.lastActivityDao();
    mAllLastActivityItems = mLastActivityDao.getLastActivityItems();
    
    // Device Profile:
    mDeviceProfileDAO = db.deviceProfileDAO();
    mAllDeviceProfiles = mDeviceProfileDAO.getDeviceProfiles();
  }
  
  public LiveData<List<Notify>> getAllPendingNotifications() {
    return mAllPendingNotifications;
  }

  public LiveData<List<Notify>> getAllRunningNotifications() {
    return mAllRunningNotifications;
  }

  public LiveData<List<Notify>> getAllCMRNotifications() {
    return mAllCMRNotifications;
  }

  public LiveData<List<Notify>> getAllCompletedNotifications() {
    return mAllCompletedNotifications;
  }
  
  public Single<Notify> getNotifyById(int id) {
    return mNotifyDao.loadNotifyById(id);
  }
  
  public Single<Notify> getNotifyByMandantTaskId(int mandantId, int taskId) {
    return mNotifyDao.loadNotifyByTaskMandantId(mandantId, taskId);
  }

  public long insert(Notify notify) {
    new insertAsyncTask(mNotifyDao).execute(notify);
    return 0;
  }
  
  public void update(Notify notify) {
    new updateAsyncTask(mNotifyDao).execute(notify);
  }
  
  public void delete(Notify notify) {
    new deleteAsyncTask(mNotifyDao).execute(notify);
  }

  public LiveData<List<LastActivity>> getAllLastActivityItems() {
    return mAllLastActivityItems;
  }
  
  public LiveData<Integer> getRowCount() {
    return mRowCount;
  }

  public void insert(LastActivity lastActivity) {
    new insertLastActivityAsyncTask(mLastActivityDao).execute(lastActivity);
  }

  public void update(LastActivity lastActivity) {
    new updateLastActivityAsyncTask(mLastActivityDao).execute(lastActivity);
  }

  public LiveData<Integer> getNotReadNotificationCount() {
    return mNotReadNotificationCount;
  }
  
  // -----------------------------------------------------------------------------------------------
  // DEVICE PROFILE:
  public void insert(DeviceProfile deviceProfile) {
    new insertDeviceProfileAsyncTask(mDeviceProfileDAO).execute(deviceProfile);
  }
  
  public void update(DeviceProfile deviceProfile) {
    new updateDeviceProfileAsyncTask(mDeviceProfileDAO).execute(deviceProfile);
  }
  
  public LiveData<List<DeviceProfile>> getAllDeviceProfiles() {
    return mAllDeviceProfiles;
  }
  // DEVICE PROFILE:
  // -----------------------------------------------------------------------------------------------

  private static class insertAsyncTask extends AsyncTask<Notify, Void, Void> {

    private NotifyDao mAsyncTaskDao;

    insertAsyncTask(NotifyDao dao) {
      mAsyncTaskDao = dao;
    }

    @Override
    protected Void doInBackground(final Notify... params) {
      long oid = mAsyncTaskDao.insertNotify(params[0]);
      if (oid > 0) {
        LastActivity lastActivity = new LastActivity();
        lastActivity.setStatusType(0);
        lastActivity.setMandantOid(params[0].getMandantId());
        lastActivity.setTaskOid(params[0].getTaskId());
        lastActivity.setOrderNo(params[0].getOrderNo());
        lastActivity.setCreatedAt(AppUtils.getCurrentDateTime());
        lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
        mLastActivityDao.insert(lastActivity);
      }
      return null;
    }
  }

  private static class updateAsyncTask extends AsyncTask<Notify, Void, Void> {

    private NotifyDao mAsyncTaskDao;

    updateAsyncTask(NotifyDao dao) {
      mAsyncTaskDao = dao;
    }

    @Override
    protected Void doInBackground(final Notify... params) {
      mAsyncTaskDao.updateNotify(params[0]);
      return null;
    }
  }
  
  private static class deleteAsyncTask extends AsyncTask<Notify, Void, Void> {
    
    private NotifyDao mAsyncTaskDao;
    
    deleteAsyncTask(NotifyDao dao) {
      mAsyncTaskDao = dao;
    }
    
    @Override
    protected Void doInBackground(final Notify... params) {
      mAsyncTaskDao.delete(params[0]);
      return null;
    }
  }

  private static class insertLastActivityAsyncTask extends AsyncTask<LastActivity, Void, Void> {

    private LastActivityDao mAsyncTaskDao;

    insertLastActivityAsyncTask(LastActivityDao dao) {
      mAsyncTaskDao = dao;
    }

    @Override
    protected Void doInBackground(final LastActivity... params) {
      mAsyncTaskDao.insert(params[0]);
      return null;
    }
  }

  private static class updateLastActivityAsyncTask extends AsyncTask<LastActivity, Void, Void> {

    private LastActivityDao mAsyncTaskDao;

    updateLastActivityAsyncTask(LastActivityDao dao) {
      mAsyncTaskDao = dao;
    }

    @Override
    protected Void doInBackground(final LastActivity... params) {
      mAsyncTaskDao.update(params[0]);
      return null;
    }
  }
  
  // -----------------------------------------------------------------------------------------------
  // DEVICE PROFILE
  // -----------------------------------------------------------------------------------------------
  
  private static class insertDeviceProfileAsyncTask extends AsyncTask<DeviceProfile, Void, Void> {
    
    private DeviceProfileDAO mDAO;
    
    insertDeviceProfileAsyncTask(DeviceProfileDAO dao) {
      mDAO = dao;
    }
    
    @Override
    protected Void doInBackground(final DeviceProfile... params) {
      mDAO.insert(params[0]);
      return null;
    }
  }
  
  private static class updateDeviceProfileAsyncTask extends AsyncTask<DeviceProfile, Void, Void> {
    
    private DeviceProfileDAO mDAO;
    
    updateDeviceProfileAsyncTask(DeviceProfileDAO dao) {
      mDAO = dao;
    }
    
    @Override
    protected Void doInBackground(final DeviceProfile... params) {
      mDAO.update(params[0]);
      return null;
    }
  }
}
