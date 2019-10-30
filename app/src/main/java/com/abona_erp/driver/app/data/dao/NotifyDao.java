package com.abona_erp.driver.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.abona_erp.driver.app.data.entity.Notify;

import java.util.List;

@Dao
public interface NotifyDao {
  
  @Query("SELECT * FROM taskItem WHERE status = 0 ORDER BY task_due_finish asc")
  public LiveData<List<Notify>> getPendingNotifies();
  
  @Query("SELECT * FROM taskItem WHERE status = 50 ORDER BY task_due_finish asc")
  public LiveData<List<Notify>> getRunningNotifies();
  
  @Query("SELECT * FROM taskItem WHERE status = 100 ORDER BY task_due_finish asc")
  public LiveData<List<Notify>> getCompletedNotifies();
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  public long insertNotify(Notify notify);
  
  @Update
  public void updateNotify(Notify notify);
  
  @Query("SELECT COUNT(id) FROM taskItem WHERE read = 0")
  public LiveData<Integer> getNotificationCount();
  
  @Query("SELECT COUNT(id) FROM taskItem WHERE status = 0")
  public LiveData<Integer> getPendingTaskCount();
  
  @Query("SELECT COUNT(id) FROM taskItem WHERE status = 50")
  public LiveData<Integer> getRunningTaskCount();
  
  @Query("SELECT COUNT(id) FROM taskItem WHERE status = 90")
  public LiveData<Integer> getCMRTaskCount();
  
  @Query("SELECT COUNT(id) FROM taskItem WHERE status = 100")
  public LiveData<Integer> getCompletedTaskCount();
}
