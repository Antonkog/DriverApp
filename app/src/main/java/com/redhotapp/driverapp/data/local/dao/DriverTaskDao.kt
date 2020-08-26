package com.redhotapp.driverapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.redhotapp.driverapp.data.local.db.TaskEntity

@Dao
interface DriverTaskDao {
    @Query("SELECT * FROM task_entity")
    fun getAll(): List<TaskEntity>

    @Delete
    fun delete(user: TaskEntity)
}