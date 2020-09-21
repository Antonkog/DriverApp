package com.abona_erp.driver.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.abona_erp.driver.app.data.entity.ChangeHistory;

import java.util.List;

@Dao
public interface ChangeHistoryDao {

  @Query("SELECT * FROM change_history ORDER BY modified_long DESC")
  LiveData<List<ChangeHistory>> getLogs();


  @Query("SELECT * FROM change_history WHERE task_id = :taskId ORDER BY modified_long DESC")
  List<ChangeHistory> getLogsByTaskId(int taskId);


  @Query("SELECT * FROM change_history WHERE task_id = :taskId ORDER BY modified_long DESC")
  LiveData<List<ChangeHistory>> getLogsWithId(int taskId);


  @Query("SELECT * FROM change_history WHERE order_number = :orderNo ORDER BY modified_long DESC")
  LiveData<List<ChangeHistory>> getLogsOrderNo(int orderNo);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long insert(ChangeHistory item);
  
  @Query("DELETE FROM change_history")
  void deleteAll();

  @Query("SELECT * FROM change_history WHERE  task_id == :taskID  AND order_number = :orderNumber AND mandant_id = :mandantId  AND  direction = :logType LIMIT 1")
  ChangeHistory selectByTypeTaskOrderMandant(int logType, int taskID, int orderNumber, int mandantId);

  @Query("SELECT * FROM change_history WHERE action_type =:actionType AND order_number ==:orderID AND task_id == :taskID AND activity_id = :activityId AND direction = :logType LIMIT 1")
  ChangeHistory selectActivityHistory(int actionType, int orderID, int activityId, int taskID, int logType);

  @Update
  int updateHistory(ChangeHistory changeHistory);

}
