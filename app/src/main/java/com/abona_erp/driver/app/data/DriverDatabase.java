package com.abona_erp.driver.app.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.abona_erp.driver.app.data.dao.LastActivityDao;
import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;

/**
 * This is the backend. The database.
 */
@Database(entities = {
  Notify.class,
  LastActivity.class
}, version = 1, exportSchema = true)
public abstract class DriverDatabase extends RoomDatabase {

  public abstract NotifyDao notifyDao();
  public abstract LastActivityDao lastActivityDao();

  // marking the instance as volatile to ensure atomic access to the variable.
  private static volatile DriverDatabase INSTANCE;

  public static DriverDatabase getDatabase(final Context context) {
    if (INSTANCE == null) {
      synchronized (DriverDatabase.class) {
        if (INSTANCE == null) {
          INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
            DriverDatabase.class, "abona_driver")
            .build();
        }
      }
    }
    return INSTANCE;
  }
}
