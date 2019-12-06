package com.abona_erp.driver.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.abona_erp.driver.app.data.entity.LastActivity;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface LastActivityDAO {
  
  @Query("SELECT * FROM last_activity_table ORDER BY modified_at DESC")
  LiveData<List<LastActivity>> getLastActivityItems();
  
  @Query("SELECT * FROM last_activity_table WHERE task_id = :task_id AND client_id = :client_id LIMIT 1")
  Single<LastActivity> getLastActivityByTaskClientId(int task_id, int client_id);
  
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  long insert(LastActivity lastActivity);
  
  @Update
  void update(LastActivity lastActivity);
  
  @Delete
  void delete(LastActivity lastActivity);
}
