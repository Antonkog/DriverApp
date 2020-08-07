package com.abona_erp.driver.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.abona_erp.driver.app.data.entity.LogItem;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface LogDAO {

  @Query("SELECT * FROM logItem where type = 8 ORDER BY id ASC")
  Single<List<LogItem>> getHistoryLogs();

  @Query("SELECT * FROM logItem where type = 2 or type = 0 ORDER BY id ASC") //see LogLevel 2 = FCM and LogLevel 32 = FATAL
  LiveData<List<LogItem>> getProtocolLogs();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long insert(LogItem item);
  
  @Query("DELETE FROM logItem")
  void deleteAll();
}
