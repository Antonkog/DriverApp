package com.abona_erp.driver.app.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.abona_erp.driver.app.data.NotifyDatabase;
import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.entity.Notify;

import java.util.List;

public class NotifyRepository {
  
  private static NotifyDatabase notifyDatabase;
  private NotifyDao notifyDao;
  private static final Object LOCK = new Object();
  
  public static synchronized NotifyDatabase getNotifyDatabase(Context context) {
    if (notifyDatabase == null) {
      synchronized (LOCK) {
        if (notifyDatabase == null) {
          notifyDatabase = Room.databaseBuilder(context,
            NotifyDatabase.class, "notify.db").build();
        }
      }
    }
    return notifyDatabase;
  }
  
  public LiveData<List<Notify>> getPendingNotifies(Context context) {
    if (notifyDao == null) {
      notifyDao = NotifyRepository.getNotifyDatabase(context).notifyDao();
    }
    return notifyDao.getPendingNotifies();
  }
  
  public LiveData<List<Notify>> getRunningNotifies(Context context) {
    if (notifyDao == null) {
      notifyDao = NotifyRepository.getNotifyDatabase(context).notifyDao();
    }
    return notifyDao.getRunningNotifies();
  }
  
  public LiveData<List<Notify>> getCompletedNotifies(Context context) {
    if (notifyDao == null) {
      notifyDao = NotifyRepository.getNotifyDatabase(context).notifyDao();
    }
    return notifyDao.getCompletedNotifies();
  }
  
  public void updateNotify(Context context, Notify notify) {
    if (notifyDao == null) {
      notifyDao = NotifyRepository.getNotifyDatabase(context).notifyDao();
    }
    notifyDao.updateNotify(notify);
  }
  
  public LiveData<Integer> getNotificationCount(Context context) {
    if (notifyDao == null) {
      notifyDao = NotifyRepository.getNotifyDatabase(context).notifyDao();
    }
    return notifyDao.getNotificationCount();
  }
  
  public LiveData<Integer> getPendingTaskCount(Context context) {
    if (notifyDao == null) {
      notifyDao = NotifyRepository.getNotifyDatabase(context).notifyDao();
    }
    return notifyDao.getPendingTaskCount();
  }
  
  public LiveData<Integer> getRunningTaskCount(Context context) {
    if (notifyDao == null) {
      notifyDao = NotifyRepository.getNotifyDatabase(context).notifyDao();
    }
    return notifyDao.getRunningTaskCount();
  }
  
  public LiveData<Integer> getCMRTaskCount(Context context) {
    if (notifyDao == null) {
      notifyDao = NotifyRepository.getNotifyDatabase(context).notifyDao();
    }
    return notifyDao.getCMRTaskCount();
  }
  
  public LiveData<Integer> getCompletedTaskCount(Context context) {
    if (notifyDao == null) {
      notifyDao = NotifyRepository.getNotifyDatabase(context).notifyDao();
    }
    return notifyDao.getCompletedTaskCount();
  }
}
