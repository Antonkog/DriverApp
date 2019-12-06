package com.abona_erp.driver.app.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.abona_erp.driver.app.data.entity.OfflineConfirmation;

import java.util.List;

@Dao
public interface OfflineConfirmationDAO {
  
  @Query("SELECT * FROM offline_confirmation ORDER BY id ASC")
  List<OfflineConfirmation> getAllOfflineConfirmations();
  
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  long insert(OfflineConfirmation offlineConfirmation);
  
  @Update
  void update(OfflineConfirmation offlineConfirmation);
  
  @Delete
  void delete(OfflineConfirmation offlineConfirmation);
}
