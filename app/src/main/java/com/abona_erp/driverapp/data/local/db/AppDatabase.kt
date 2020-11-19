package com.abona_erp.driverapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abona_erp.driverapp.data.local.dao.*

@Database(
    entities = [ActivityEntity::class, TaskEntity::class, DocumentEntity::class, ChangeHistory::class, DelayReasonEntity::class],
    version = 1,//while app in not in main branch - do complete reinstall, inform about db change to all developers do not upgrade, exportSchema = false
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun driverActDao(): DriverActDao
    abstract fun driverTaskDao(): DriverTaskDao
    abstract fun documentsDao(): DocumentsDao
    abstract fun changeHistoryDao(): ChangeHistoryDao
    abstract fun delayReasonsDao(): DelayReasonsDao
}