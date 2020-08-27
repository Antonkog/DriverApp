package com.redhotapp.driverapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.redhotapp.driverapp.data.local.db.ActivityEntity

@Dao
interface DriverActDao {
    @Query("SELECT * FROM activity_entity")
    fun getAll(): LiveData<List<ActivityEntity>>

    @Delete
    fun delete(user: ActivityEntity)
}