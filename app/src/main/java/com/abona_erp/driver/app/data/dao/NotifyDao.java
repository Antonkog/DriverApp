package com.abona_erp.driver.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.abona_erp.driver.app.data.entity.Notify;

import java.util.List;

@Dao
public interface NotifyDao {
  
  @Query("SELECT * FROM notify")
  public LiveData<List<Notify>> getNotifies();
  
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  public long insertNotify(Notify notify);
}
