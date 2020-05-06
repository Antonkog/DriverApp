package com.abona_erp.driver.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.abona_erp.driver.app.data.entity.LogItem;

import java.util.List;

@Dao
public interface LogDAO {
  
  @Query("SELECT * FROM logItem ORDER BY id ASC")
  LiveData<List<LogItem>> getAllLogs();
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long insert(LogItem item);
  
  @Query("DELETE FROM logItem")
  void deleteAll();
}
