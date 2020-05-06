package com.abona_erp.driver.app.data;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.abona_erp.driver.app.data.converters.Converters;
import com.abona_erp.driver.app.data.dao.DeviceProfileDAO;
import com.abona_erp.driver.app.data.dao.LastActivityDAO;
import com.abona_erp.driver.app.data.dao.LogDAO;
import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.dao.OfflineConfirmationDAO;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.LogItem;
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
  OfflineConfirmation.class,
  LogItem.class
}, version = 3, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class DriverDatabase extends RoomDatabase {

  public abstract NotifyDao notifyDao();
  public abstract LastActivityDAO lastActivityDAO();
  public abstract DeviceProfileDAO deviceProfileDAO();
  public abstract OfflineConfirmationDAO offlineConfirmationDAO();
  public abstract LogDAO logDAO();

  // marking the instance as volatile to ensure atomic access to the variable.
  private static volatile DriverDatabase INSTANCE;

  public static DriverDatabase getDatabase() {
    if (INSTANCE == null) {
      synchronized (DriverDatabase.class) {
        if (INSTANCE == null) {
          INSTANCE = Room.databaseBuilder(ContextUtils.getApplicationContext(),
            DriverDatabase.class, "abona_driver77")
            .allowMainThreadQueries()
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build();
        }
      }
    }
    return INSTANCE;
  }
  
  static final Migration MIGRATION_2_3 = new Migration(2, 3) {
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
      database.execSQL("CREATE TABLE IF NOT EXISTS `logItem` (`id` integer NOT NULL primary key autoincrement, `level` integer, `title` varchar(255), `message` text, `type` integer, `created_at` TEXT)");
    }
  };
  
  static final Migration MIGRATION_1_2 = new Migration(1, 2) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
      database.execSQL("ALTER TABLE taskItem "
        + " ADD COLUMN document_urls TEXT");
    }
  };
}
