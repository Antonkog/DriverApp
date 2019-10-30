package com.abona_erp.driver.app.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.entity.Notify;

@Database(entities = {Notify.class}, version = 1, exportSchema = false)
public abstract class NotifyDatabase extends RoomDatabase {
  public abstract NotifyDao notifyDao();
}
