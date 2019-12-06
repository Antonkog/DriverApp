package com.abona_erp.driver.app.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.abona_erp.driver.app.data.entity.DeviceProfile;

import java.util.List;

@Dao
public interface DeviceProfileDAO {
  
  @Query("SELECT * FROM device_profile ORDER BY created_at DESC")
  List<DeviceProfile> getDeviceProfiles();
  
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  long insert(DeviceProfile deviceProfile);
  
  @Update
  void update(DeviceProfile deviceProfile);
}
