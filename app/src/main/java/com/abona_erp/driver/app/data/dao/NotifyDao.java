package com.abona_erp.driver.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.abona_erp.driver.app.data.entity.Notify;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface NotifyDao {
  
  @Query("SELECT * FROM taskItem WHERE status = 0 ORDER BY task_due_finish ASC")
  LiveData<List<Notify>> getAllPendingNotifications();
  
  @Query("SELECT * FROM taskItem WHERE status = 50 ORDER BY task_due_finish ASC")
  LiveData<List<Notify>> getAllRunningNotifications();

  @Query("SELECT * FROM taskItem WHERE status = 90 ORDER BY task_due_finish ASC")
  LiveData<List<Notify>> getAllCMRNotifications();
  
  @Query("SELECT * FROM taskItem WHERE status = 100 ORDER BY task_due_finish ASC")
  LiveData<List<Notify>> getAllCompletedNotifications();
  
  @Query("SELECT * FROM taskItem WHERE id = :id LIMIT 1")
  Single<Notify> loadNotifyById(int id);
  
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  void insertNotify(Notify notify);
  
  @Update
  void updateNotify(Notify notify);

  @Query("SELECT COUNT(id) FROM taskItem WHERE read = 0")
  LiveData<Integer> getNotReadNotificationCount();
}
