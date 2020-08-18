package com.abona_erp.driver.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.abona_erp.driver.app.data.entity.LogItem;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface LogDAO {

  @Query("SELECT * FROM logItem  where type != 0 ORDER BY id DESC")
  LiveData<List<LogItem>> getLogs();

  @Query("SELECT * FROM logItem where type = 0") //see LogLevel 2 = FCM and LogLevel 32 = FATAL
  LiveData<List<LogItem>> getProtocolLogs();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long insert(LogItem item);
  
  @Query("DELETE FROM logItem")
  void deleteAll();

  @Query("SELECT COUNT(id) from logItem")
  Flowable<Integer> countLogItems();

  @Query("DELETE FROM logItem WHERE id NOT IN (SELECT  MAX(id) FROM logItem GROUP BY level, title, message,type)")
  void groupResponses();
}
