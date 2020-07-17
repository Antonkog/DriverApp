package com.abona_erp.driver.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.abona_erp.driver.app.data.entity.DelayReasonEntity;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface DelayReasonDAO {
  
  @Query("SELECT * FROM delay_reason_table WHERE MandantId = :mandantId AND ActivityId = :activityId ORDER BY WaitingReasonId ASC")
  List<DelayReasonEntity> getDelayReasonsByMandantIdAndActivityId(int mandantId, int activityId);
  
  @Query("SELECT * FROM delay_reason_table WHERE MandantId = :mandantId AND ActivityId = :activityId ORDER BY WaitingReasonId ASC")
  LiveData<List<DelayReasonEntity>> getActivityDelayReasonsByMandantIdAndActivityId(int mandantId, int activityId);
  
  @Query("SELECT * FROM delay_reason_table WHERE MandantId = :mandantId AND ActivityId = :activityId AND WaitingReasonId = :waitingReasonId LIMIT 1")
  Single<DelayReasonEntity> getDelayReasonByMandantId(int mandantId, int activityId, int waitingReasonId);
  
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  long insert(DelayReasonEntity delayReasonEntity);
  
  @Update
  void update(DelayReasonEntity delayReasonEntity);
  
  @Delete
  void delete(DelayReasonEntity delayReasonEntity);
  
  @Query("DELETE FROM delay_reason_table")
  void deleteAll();
}
