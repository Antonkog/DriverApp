package com.abona_erp.driver.app.data.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.abona_erp.driver.app.App;
import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.dao.DeviceProfileDAO;
import com.abona_erp.driver.app.data.dao.LastActivityDAO;
import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.dao.OfflineConfirmationDAO;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.model.CommItem;
import com.abona_erp.driver.app.data.model.ConfirmationType;
import com.abona_erp.driver.app.data.model.LastActivityDetails;
import com.abona_erp.driver.app.data.model.TaskActionType;
import com.abona_erp.driver.app.data.model.TaskStatus;
import com.abona_erp.driver.app.util.AppUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Single;

public class DriverRepository {

  private NotifyDao mNotifyDao;
  private LiveData<List<Notify>> mAllPendingNotifications;
  private LiveData<List<Notify>> mAllRunningNotifications;
  private LiveData<List<Notify>> mAllCMRNotifications;
  private LiveData<List<Notify>> mAllCompletedNotifications;
  private LiveData<Integer> mNotReadNotificationCount;
  private LiveData<Integer> mRowCount;

  private static LastActivityDAO mLastActivityDAO;
  private LiveData<List<LastActivity>> mAllLastActivityItems;
  
  private DeviceProfileDAO mDeviceProfileDAO;
  //private List<DeviceProfile> mAllDeviceProfiles;
  
  private OfflineConfirmationDAO mOfflineConfirmationDAO;
  private LiveData<List<OfflineConfirmation>> mAllOfflineConfirmation;
  
  public DriverRepository(Application application) {
    DriverDatabase db = DriverDatabase.getDatabase();
    mNotifyDao = db.notifyDao();
    mAllPendingNotifications = mNotifyDao.getAllPendingNotifications();
    mAllRunningNotifications = mNotifyDao.getAllRunningNotifications();
    mAllCMRNotifications = mNotifyDao.getAllCMRNotifications();
    mAllCompletedNotifications = mNotifyDao.getAllCompletedNotifications();
    mNotReadNotificationCount = mNotifyDao.getNotReadNotificationCount();
    mRowCount = mNotifyDao.getRowCount();

    mLastActivityDAO = db.lastActivityDAO();
    mAllLastActivityItems = mLastActivityDAO.getLastActivityItems();
    
    // Device Profile:
    mDeviceProfileDAO = db.deviceProfileDAO();
    //mAllDeviceProfiles = mDeviceProfileDAO.getDeviceProfiles();
    
    mOfflineConfirmationDAO = db.offlineConfirmationDAO();
    mAllOfflineConfirmation = mOfflineConfirmationDAO.getAllLiveDataConfirmations();
  }
  
  public LiveData<List<OfflineConfirmation>> getAllLiveDataConfirmations() {
    return mAllOfflineConfirmation;
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
  
  public Single<LastActivity> getLastActivityByTaskClientId(int taskId, int clientId) {
    return mLastActivityDAO.getLastActivityByTaskClientId(taskId, clientId);
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
  
  public void deleteAllNotify() {
    new deleteAllNotifyAsyncTask(mNotifyDao).execute();
  }

  public LiveData<List<LastActivity>> getAllLastActivityItems() {
    return mAllLastActivityItems;
  }
  
  public LiveData<Integer> getRowCount() {
    return mRowCount;
  }

  public void insert(LastActivity lastActivity) {
    new insertLastActivityAsyncTask(mLastActivityDAO).execute(lastActivity);
  }

  public void update(LastActivity lastActivity) {
    new updateLastActivityAsyncTask(mLastActivityDAO).execute(lastActivity);
  }
  
  public void delete(LastActivity lastActivity) {
    new deleteLastActivityAsyncTask(mLastActivityDAO).execute(lastActivity);
  }
  
  public void deleteAllLastActivities() {
    mLastActivityDAO.deleteAll();
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
  
  //public List<DeviceProfile> getAllDeviceProfiles() {
  //  return mAllDeviceProfiles;
  //}
  // DEVICE PROFILE:
  // -----------------------------------------------------------------------------------------------

  private static class insertAsyncTask extends AsyncTask<Notify, Void, Void> {

    private NotifyDao mDAO;

    insertAsyncTask(NotifyDao dao) {
      mDAO = dao;
    }

    @Override
    protected Void doInBackground(final Notify... params) {
      long oid = mDAO.insertNotify(params[0]);
      if (oid > 0) {
        DriverDatabase db = DriverDatabase.getDatabase();
        OfflineConfirmationDAO dao = db.offlineConfirmationDAO();
  
        OfflineConfirmation offlineConfirmation = new OfflineConfirmation();
        offlineConfirmation.setNotifyId((int) oid);
        offlineConfirmation.setConfirmType(ConfirmationType.TASK_CONFIRMED_BY_DEVICE.ordinal());
        dao.insert(offlineConfirmation);
        
        CommItem commItem = App.getGson().fromJson(params[0].getData(), CommItem.class);
        if (commItem != null) {
          /*
          if (commItem.getTaskItem().getActivities().size() > 0) {
            for (int i = 0; i < commItem.getTaskItem().getActivities().size(); i++) {
              OfflineConfirmation _offline = new OfflineConfirmation();
              _offline.setNotifyId((int)oid);
              _offline.setConfirmType(ConfirmationType.ACTIVITY_CONFIRMED_BY_DEVICE.ordinal());
              dao.insert(_offline);
            }
          }
          */
          
          // Add Last Activity DB Item:
          LastActivity lastActivity = new LastActivity();
          lastActivity.setTaskId(commItem.getTaskItem().getTaskId());
          lastActivity.setClientId(commItem.getTaskItem().getMandantId());
          lastActivity.setCustomer(commItem.getTaskItem().getKundenName());
          lastActivity.setOrderNo(AppUtils.parseOrderNo(commItem.getTaskItem().getOrderNo()));
          lastActivity.setStatusType(0);
          lastActivity.setConfirmStatus(0);
          if (commItem.getTaskItem().getTaskStatus().equals(TaskStatus.PENDING) || commItem.getTaskItem().getTaskStatus().equals(TaskStatus.RUNNING)) {
            lastActivity.setVisible(true);
          } else {
            lastActivity.setVisible(false);
          }
          if (commItem.getTaskItem().getActionType().equals(TaskActionType.PICK_UP)) {
            lastActivity.setTaskActionType(0);
          } else if (commItem.getTaskItem().getActionType().equals(TaskActionType.DROP_OFF)) {
            lastActivity.setTaskActionType(1);
          } else if (commItem.getTaskItem().getActionType().equals(TaskActionType.TRACTOR_SWAP)) {
            lastActivity.setTaskActionType(3);
          } else if (commItem.getTaskItem().getActionType().equals(TaskActionType.GENERAL)) {
            lastActivity.setTaskActionType(2);
          } else if (commItem.getTaskItem().getActionType().equals(TaskActionType.DELAY)) {
            lastActivity.setTaskActionType(4);
          } else if (commItem.getTaskItem().getActionType().equals(TaskActionType.UNKNOWN)) {
            lastActivity.setTaskActionType(100);
          }
          synchronized (this) {
            
            lastActivity.setCreatedAt(AppUtils.getCurrentDateTime());
            lastActivity.setModifiedAt(AppUtils.getCurrentDateTime());
  
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss",
              Locale.getDefault());
            LastActivityDetails _detail = new LastActivityDetails();
            _detail.setDescription("NEU");
            _detail.setTimestamp(sdf.format(AppUtils.getCurrentDateTime()));
            ArrayList<String> _list = new ArrayList<>();
            _list.add(App.getGson().toJson(_detail));
            lastActivity.setDetailList(_list);
          }
          mLastActivityDAO.insert(lastActivity);
        }
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
  
  private static class deleteAllNotifyAsyncTask extends AsyncTask<Void, Void, Void> {
    
    private NotifyDao mAsyncTaskDao;
    
    deleteAllNotifyAsyncTask(NotifyDao dao) {
      mAsyncTaskDao = dao;
    }
    
    @Override
    protected Void doInBackground(Void... params) {
      mAsyncTaskDao.deleteAll();
      return null;
    }
  }

  private static class insertLastActivityAsyncTask extends AsyncTask<LastActivity, Void, Void> {

    private LastActivityDAO mDAO;

    insertLastActivityAsyncTask(LastActivityDAO dao) {
      mDAO = dao;
    }

    @Override
    protected Void doInBackground(final LastActivity... params) {
      mDAO.insert(params[0]);
      return null;
    }
  }

  private static class updateLastActivityAsyncTask extends AsyncTask<LastActivity, Void, Void> {

    private LastActivityDAO mDAO;

    updateLastActivityAsyncTask(LastActivityDAO dao) {
      mDAO = dao;
    }

    @Override
    protected Void doInBackground(final LastActivity... params) {
      mDAO.update(params[0]);
      return null;
    }
  }
  
  private static class deleteLastActivityAsyncTask extends AsyncTask<LastActivity, Void, Void> {
    
    private LastActivityDAO mDAO;
    
    deleteLastActivityAsyncTask(LastActivityDAO dao) {
      mDAO = dao;
    }
    
    @Override
    protected Void doInBackground(final LastActivity... params) {
      mDAO.delete(params[0]);
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
