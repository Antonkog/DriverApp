package com.abona_erp.driver.app.data.local.db

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "activity_entity", indices = arrayOf(Index(value = ["taskpId","mandantId" ])),
    foreignKeys = [
        ForeignKey(entity = TaskEntity::class,
            parentColumns = ["taskId","mandantId"],
            childColumns = ["taskpId","mandantId"],
            onDelete = CASCADE)],
    primaryKeys = ["taskpId", "activityId", "mandantId"]
)

data class ActivityEntity(
    @ColumnInfo val activityId: Int,
    @ColumnInfo val customActivityId: Int,
    @ColumnInfo val delayReasons : List<DelayReasonEntity>?,
    @ColumnInfo val description : String?,
    @ColumnInfo val finished: String?,
    @ColumnInfo val mandantId: Int,
    @ColumnInfo val name: String?,
    @ColumnInfo val radiusGeoFence: Int,
    @ColumnInfo val sequence: Int,
    @ColumnInfo val taskpId: Int,
    @ColumnInfo val started: String?,
    @ColumnInfo val status: ConfirmationType
)