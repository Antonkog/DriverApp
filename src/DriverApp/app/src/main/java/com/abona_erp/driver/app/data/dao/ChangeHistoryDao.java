package com.abona_erp.driver.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.abona_erp.driver.app.data.entity.ChangeHistory;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface ChangeHistoryDao {

  @Query("SELECT * FROM change_history ORDER BY modified_long DESC")
  LiveData<List<ChangeHistory>> getLogs();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long insert(ChangeHistory item);
  
  @Query("DELETE FROM change_history")
  void deleteAll();

  @Query("SELECT * FROM change_history WHERE direction = :logType and task_id == :taskID  and order_number = :orderNumber and mandant_id = :mandantId LIMIT 1")
  ChangeHistory selectByTypeTaskOrderMandant(int logType, int taskID, int orderNumber, int mandantId);

  @Update
  void updateHistory(ChangeHistory changeHistory);

  @Query("SELECT COUNT(id) from change_history")
  Flowable<Integer> countLogItems();
}
