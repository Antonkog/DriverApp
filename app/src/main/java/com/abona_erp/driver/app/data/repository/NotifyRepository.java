package com.abona_erp.driver.app.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.abona_erp.driver.app.data.NotifyDatabase;
import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.entity.Notify;

import java.util.List;

public class NotifyRepository {
  
  private static NotifyDatabase notifyDatabase;
  private NotifyDao notifyDao;
  private static final Object LOCK = new Object();
  
  public synchronized static NotifyDatabase getNotifyDatabase(Context context) {
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
  
  public LiveData<List<Notify>> getNotifies(Context context) {
    if (notifyDao == null) {
      notifyDao = NotifyRepository.getNotifyDatabase(context).notifyDao();
    }
    return notifyDao.getNotifies();
  }
}
