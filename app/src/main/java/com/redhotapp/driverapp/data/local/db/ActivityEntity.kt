package com.redhotapp.driverapp.data.local.db

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "activity_entity", indices = arrayOf(Index(value = ["activityId", "mandantId", "taskpId"])),
    foreignKeys = [
        ForeignKey(entity = TaskEntity::class,
            parentColumns = ["taskId"],
            childColumns = ["taskpId"],
            onDelete = CASCADE)])

data class ActivityEntity(
    @PrimaryKey (autoGenerate = true)  val id: Int,
    @ColumnInfo val activityId: Int,
    @ColumnInfo val mandantId: Int,
    @ColumnInfo val taskpId: Int,
    @ColumnInfo val deviceId: Int, // to send
    @ColumnInfo val started: String?,
    @ColumnInfo val finished: String?,
    @ColumnInfo val name: String?
)

