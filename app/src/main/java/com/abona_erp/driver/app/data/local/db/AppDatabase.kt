package com.abona_erp.driver.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abona_erp.driver.app.data.local.dao.DriverActDao
import com.abona_erp.driver.app.data.local.dao.DriverTaskDao

@Database(entities = [ActivityEntity::class, TaskEntity::class], version = 1, exportSchema = false)
@TypeConverters(GsonToDbConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun driverActDao(): DriverActDao
    abstract fun driverTaskDao(): DriverTaskDao
}