package com.abona_erp.driver.app.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.abona_erp.driver.app.data.converters.Converters;
import com.abona_erp.driver.app.data.dao.DeviceProfileDAO;
import com.abona_erp.driver.app.data.dao.LastActivityDAO;
import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.dao.OfflineConfirmationDAO;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.core.base.ContextUtils;

/**
 * This is the backend. The database.
 */
@Database(entities = {
  Notify.class,
  LastActivity.class,
  DeviceProfile.class,
  OfflineConfirmation.class
}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class DriverDatabase extends RoomDatabase {

  public abstract NotifyDao notifyDao();
  public abstract LastActivityDAO lastActivityDAO();
  public abstract DeviceProfileDAO deviceProfileDAO();
  public abstract OfflineConfirmationDAO offlineConfirmationDAO();

  // marking the instance as volatile to ensure atomic access to the variable.
  private static volatile DriverDatabase INSTANCE;

  public static DriverDatabase getDatabase() {
    if (INSTANCE == null) {
      synchronized (DriverDatabase.class) {
        if (INSTANCE == null) {
          INSTANCE = Room.databaseBuilder(ContextUtils.getApplicationContext(),
            DriverDatabase.class, "abona_driver00")
            .build();
        }
      }
    }
    return INSTANCE;
  }
}
