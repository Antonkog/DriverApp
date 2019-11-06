package com.abona_erp.driver.app.data.repository;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.abona_erp.driver.app.data.DriverDatabase;
import com.abona_erp.driver.app.data.dao.LastActivityDao;
import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;

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

  private LastActivityDao mLastActivityDao;
  private LiveData<List<LastActivity>> mAllLastActivityItems;
  
  public DriverRepository(Application application) {
    DriverDatabase db = DriverDatabase.getDatabase(application);
    mNotifyDao = db.notifyDao();
    mAllPendingNotifications = mNotifyDao.getAllPendingNotifications();
    mAllRunningNotifications = mNotifyDao.getAllRunningNotifications();
    mAllCMRNotifications = mNotifyDao.getAllCMRNotifications();
    mAllCompletedNotifications = mNotifyDao.getAllCompletedNotifications();
    mNotReadNotificationCount = mNotifyDao.getNotReadNotificationCount();

    mLastActivityDao = db.lastActivityDao();
    mAllLastActivityItems = mLastActivityDao.getLastActivityItems();
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

  public void insert(Notify notify) {
    new insertAsyncTask(mNotifyDao).execute(notify);
  }
  
  public void update(Notify notify) {
    new updateAsyncTask(mNotifyDao).execute(notify);
  }

  public LiveData<List<LastActivity>> getAllLastActivityItems() {
    return mAllLastActivityItems;
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

  private static class insertAsyncTask extends AsyncTask<Notify, Void, Void> {

    private NotifyDao mAsyncTaskDao;

    insertAsyncTask(NotifyDao dao) {
      mAsyncTaskDao = dao;
    }

    @Override
    protected Void doInBackground(final Notify... params) {
      mAsyncTaskDao.insertNotify(params[0]);
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
}
