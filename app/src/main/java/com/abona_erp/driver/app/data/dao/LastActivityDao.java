package com.abona_erp.driver.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.abona_erp.driver.app.data.entity.LastActivity;

import java.util.List;

@Dao
public interface LastActivityDao {

  @Query("SELECT * FROM last_activity_table ORDER BY created_at DESC")
  LiveData<List<LastActivity>> getLastActivityItems();

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  long insert(LastActivity history);

  @Update
  void update(LastActivity activity);

  //@Query("DELETE FROM last_activity_table")
  //void deleteAll();
}
