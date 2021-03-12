package com.abona_erp.driver.app.data;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.abona_erp.driver.app.data.converters.Converters;
import com.abona_erp.driver.app.data.dao.ChangeHistoryDao;
import com.abona_erp.driver.app.data.dao.DelayReasonDAO;
import com.abona_erp.driver.app.data.dao.DeviceProfileDAO;
import com.abona_erp.driver.app.data.dao.LastActivityDAO;
import com.abona_erp.driver.app.data.dao.LogDAO;
import com.abona_erp.driver.app.data.dao.NotifyDao;
import com.abona_erp.driver.app.data.dao.OfflineConfirmationDAO;
import com.abona_erp.driver.app.data.dao.OfflineDelayReasonDAO;
import com.abona_erp.driver.app.data.entity.ChangeHistory;
import com.abona_erp.driver.app.data.entity.DelayReasonEntity;
import com.abona_erp.driver.app.data.entity.DeviceProfile;
import com.abona_erp.driver.app.data.entity.LastActivity;
import com.abona_erp.driver.app.data.entity.LogItem;
import com.abona_erp.driver.app.data.entity.Notify;
import com.abona_erp.driver.app.data.entity.OfflineConfirmation;
import com.abona_erp.driver.app.data.entity.OfflineDelayReasonEntity;
import com.abona_erp.driver.core.base.ContextUtils;

/**
 * This is the backend. The database.
 */
@Database(entities = {
  Notify.class,
  LastActivity.class,
  DeviceProfile.class,
  OfflineConfirmation.class,
  LogItem.class,
  DelayReasonEntity.class,
  OfflineDelayReasonEntity.class,
  ChangeHistory.class
}, version = 11, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class DriverDatabase extends RoomDatabase {

  public abstract NotifyDao notifyDao();
  public abstract LastActivityDAO lastActivityDAO();
  public abstract DeviceProfileDAO deviceProfileDAO();
  public abstract OfflineConfirmationDAO offlineConfirmationDAO();
  public abstract LogDAO logDAO();
  public abstract ChangeHistoryDao changeHistoryDao();
  public abstract DelayReasonDAO delayReasonDAO();
  public abstract OfflineDelayReasonDAO offlineDelayReasonDAO();

  // marking the instance as volatile to ensure atomic access to the variable.
  private static volatile DriverDatabase INSTANCE;

  public static DriverDatabase getDatabase() {
    if (INSTANCE == null) {
      synchronized (DriverDatabase.class) {
        if (INSTANCE == null) {
          INSTANCE = Room.databaseBuilder(ContextUtils.getApplicationContext(),
            DriverDatabase.class, "abona_driver77")
            .allowMainThreadQueries()
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11)
            .build();
        }
      }
    }
    return INSTANCE;
  }
  
  static final Migration MIGRATION_10_11 = new Migration(10, 11) {
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
      database.execSQL("ALTER TABLE offline_confirmation "
        + " ADD COLUMN activity_status integer DEFAULT 0 NOT NULL");
    }
  };
  
  static final Migration MIGRATION_9_10 = new Migration(9, 10) {
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
      database.execSQL("ALTER TABLE offline_delay_reason_entity "
        + " ADD COLUMN in_progress integer DEFAULT 0 NOT NULL");
    }
  };

  static final Migration MIGRATION_8_9 = new Migration(8, 9) {
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
      database.execSQL("ALTER TABLE offline_delay_reason_entity "
        + " ADD COLUMN WaitingReasonAppId text");
    }
  };

  static final Migration MIGRATION_7_8 = new Migration(7, 8) {
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
      database.execSQL("CREATE TABLE IF NOT EXISTS `change_history` (`id` INTEGER NOT NULL primary key autoincrement, " +
              "`title` varchar(255), `message` text, `direction` integer,  `action_type` integer, `state` integer, `created_at` text, `modified_at` text, " +
              "`modified_long` INTEGER  NOT NULL, `task_id` integer  NOT NULL, `activity_id` integer  NOT NULL, `order_number` integer NOT NULL, `mandant_id` integer NOT NULL, `confirmation_id` integer  NOT NULL)");

    }
  };

  static final Migration MIGRATION_6_7 = new Migration(6, 7) {
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
      database.execSQL("ALTER TABLE offline_confirmation "
        + " ADD COLUMN upload_flag INTEGER DEFAULT 0 NOT NULL");
    }
  };

  static final Migration MIGRATION_5_6 = new Migration(5, 6) {
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
      database.execSQL("DELETE FROM logItem WHERE id >0");
      database.execSQL("ALTER TABLE logItem "
              + " ADD COLUMN task_id INTEGER DEFAULT 0 NOT NULL");
    }
  };

  static final Migration MIGRATION_4_5 = new Migration(4, 5) {
    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
      database.execSQL("ALTER TABLE taskItem "
        + " ADD COLUMN confirmation_status INTEGER DEFAULT 0 NOT NULL");
      database.execSQL("CREATE TABLE IF NOT EXISTS `delay_reason_table` (`id` INTEGER NOT NULL primary key AUTOINCREMENT , `MandantId` INTEGER DEFAULT 0 NOT NULL , `ActivityId` INTEGER DEFAULT 0 NOT NULL , `WaitingReasonId` INTEGER DEFAULT 0 NOT NULL , `ReasonText` TEXT, `TranslatedReasonText` TEXT, `Code` INTEGER DEFAULT 0 NOT NULL, `SubCode` INTEGER DEFAULT 0 NOT NULL,  `ModifiedAt` TEXT, `CreatedAt` TEXT)");
      database.execSQL("CREATE TABLE IF NOT EXISTS `offline_delay_reason_entity` (`id` INTEGER NOT NULL primary key autoincrement, `notify_id` INTEGER DEFAULT 0 NOT NULL, `waiting_reason_id` INTEGER DEFAULT 0 NOT NULL, `activity_id` INTEGER DEFAULT 0 NOT NULL, `mandant_id` INTEGER DEFAULT 0 NOT NULL, `task_id` INTEGER DEFAULT 0 NOT NULL, `delay_in_minutes` integer DEFAULT 0 NOT NULL, `delay_source` INTEGER DEFAULT 0 NOT NULL, `comment` TEXT, `timestamp` TEXT)");
    }
  };

  static final Migration MIGRATION_3_4 = new Migration(3, 4) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
      database.execSQL("ALTER TABLE taskItem "
              + " ADD COLUMN currently_selected INTEGER DEFAULT 0 NOT NULL");
    }
  };

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
