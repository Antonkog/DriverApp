package com.redhotapp.driverapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.redhotapp.driverapp.data.local.dao.DriverActDao
import com.redhotapp.driverapp.data.local.dao.DriverTaskDao

@Database(entities = arrayOf(ActivityEntity::class, TaskEntity::class), version = 1, exportSchema = false)
@TypeConverters(GsonToDbConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun driverActDao(): DriverActDao
    abstract fun driverTaskDao(): DriverTaskDao
}