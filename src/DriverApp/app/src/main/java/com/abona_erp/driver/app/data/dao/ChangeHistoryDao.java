package com.abona_erp.driver.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.abona_erp.driver.app.data.entity.ChangeHistory;
import com.abona_erp.driver.app.data.entity.LogItem;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface ChangeHistoryDao {

  @Query("SELECT * FROM change_history ORDER BY id DESC")
  LiveData<List<ChangeHistory>> getLogs();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long insert(ChangeHistory item);
  
  @Query("DELETE FROM change_history")
  void deleteAll();

  @Query("SELECT COUNT(id) from change_history")
  Flowable<Integer> countLogItems();
}
