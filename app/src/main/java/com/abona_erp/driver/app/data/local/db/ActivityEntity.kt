package com.abona_erp.driver.app.data.local.db

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "activity_entity", indices = arrayOf(Index(value = ["taskpId"])),
    foreignKeys = [
        ForeignKey(entity = TaskEntity::class,
            parentColumns = ["taskId","mandantId"],
            childColumns = ["taskpId","mandantId"],
            onDelete = CASCADE)],
    primaryKeys = ["taskpId", "activityId", "mandantId"]
)

data class ActivityEntity(
    @ColumnInfo val activityId: Int,
    @ColumnInfo val mandantId: Int,
    @ColumnInfo val taskpId: Int,
    @ColumnInfo val deviceId: String?, // to send
    @ColumnInfo val started: String?,
    @ColumnInfo val finished: String?,
    @ColumnInfo val name: String?
)

