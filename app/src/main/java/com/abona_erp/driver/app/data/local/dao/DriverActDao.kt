package com.abona_erp.driver.app.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.abona_erp.driver.app.data.local.db.ActivityEntity

@Dao
interface DriverActDao {
    @Query("SELECT * FROM activity_entity")
    fun getAll(): LiveData<List<ActivityEntity>>

    @Delete
    fun delete(user: ActivityEntity)
}