package com.abona_erp.driverapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abona_erp.driverapp.data.local.dao.DocumentsDao
import com.abona_erp.driverapp.data.local.dao.DriverActDao
import com.abona_erp.driverapp.data.local.dao.DriverTaskDao

@Database(
    entities = [ActivityEntity::class, TaskEntity::class, DocumentEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun driverActDao(): DriverActDao
    abstract fun driverTaskDao(): DriverTaskDao
    abstract fun documentsDao(): DocumentsDao
}