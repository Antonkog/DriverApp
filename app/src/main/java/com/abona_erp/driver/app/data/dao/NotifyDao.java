package com.abona_erp.driver.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.abona_erp.driver.app.data.entity.Notify;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface NotifyDao {
  
  //@Query("SELECT * FROM taskItem WHERE status = 0 ORDER BY task_due_finish ASC")
  //@Query("SELECT * FROM taskItem WHERE status = 0 ORDER BY task_due_finish AND order_no AND task_id ASC")
  @Query("SELECT * FROM taskItem WHERE status = 0 ORDER BY order_no AND task_id ASC")
  LiveData<List<Notify>> getAllPendingNotifications();
  
  @Query("SELECT * FROM taskItem WHERE status = 50 ORDER BY task_due_finish AND order_no AND task_id ASC")
  LiveData<List<Notify>> getAllRunningNotifications();

  @Query("SELECT * FROM taskItem WHERE status = 90 ORDER BY task_due_finish AND order_no AND task_id ASC")
  LiveData<List<Notify>> getAllCMRNotifications();
  
  @Query("SELECT * FROM taskItem WHERE status = 100 ORDER BY task_due_finish AND order_no AND task_id ASC")
  LiveData<List<Notify>> getAllCompletedNotifications();
  
  @Query("SELECT * FROM taskItem WHERE id = :id LIMIT 1")
  Single<Notify> loadNotifyById(int id);
  
  @Query("SELECT * FROM taskItem WHERE mandant_id = :mandantId AND task_id = :taskId LIMIT 1")
  Single<Notify> loadNotifyByTaskMandantId(int mandantId, int taskId);
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long insertNotify(Notify notify);
  
  @Update
  void updateNotify(Notify notify);
  
  @Delete
  void delete(Notify notify);

  @Query("SELECT COUNT(id) FROM taskItem WHERE read = 0")
  LiveData<Integer> getNotReadNotificationCount();
  
  @Query("SELECT COUNT(id) FROM taskItem")
  LiveData<Integer> getRowCount();
  
  @Query("DELETE FROM taskItem")
  void deleteAll();
}
