package com.abona_erp.driver.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.abona_erp.driver.app.data.entity.OfflineDelayReasonEntity;

import java.util.List;

@Dao
public interface OfflineDelayReasonDAO {
  
  @Query("SELECT * FROM offline_delay_reason_entity ORDER BY id ASC")
  List<OfflineDelayReasonEntity> getAllOfflineDelayReasons();
  
  @Query("SELECT * FROM offline_delay_reason_entity ORDER BY id ASC")
  LiveData<List<OfflineDelayReasonEntity>> getAllLiveDataOfflineDelayReasons();
  
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  long insert(OfflineDelayReasonEntity offlineDelayReasonEntity);
  
  @Update
  void update(OfflineDelayReasonEntity offlineDelayReasonEntity);
  
  @Delete
  void delete(OfflineDelayReasonEntity offlineDelayReasonEntity);
  
  @Query("DELETE FROM offline_delay_reason_entity")
  void deleteAll();
}
