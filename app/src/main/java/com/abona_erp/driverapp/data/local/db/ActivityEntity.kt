package com.abona_erp.driverapp.data.local.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.NO_ACTION
import androidx.room.Index
import com.abona_erp.driverapp.data.model.ActivityStatus
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName = "activity_entity", indices = arrayOf(Index(value = ["taskpId", "mandantId"])),
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["taskId", "mandantId"],
            childColumns = ["taskpId", "mandantId"],
            onDelete = NO_ACTION
        )],
    primaryKeys = ["taskpId", "activityId", "mandantId"]
)
//            onDelete = CASCADE not using as we have custom update logic

@Parcelize
data class ActivityEntity(
    @ColumnInfo val activityId: Int,
    @ColumnInfo val customActivityId: Int,
    @ColumnInfo val delayReasons: List<DelayReasonEntity>?,
    @ColumnInfo val description: String?,
    @ColumnInfo val mandantId: Int,
    @ColumnInfo val name: String?,
    @ColumnInfo val radiusGeoFence: Int,
    @ColumnInfo val sequence: Int,
    @ColumnInfo val taskpId: Int,
    @ColumnInfo val started: Long,
    @ColumnInfo val finished: Long,
    @ColumnInfo val activityStatus: ActivityStatus,
    @ColumnInfo val confirmationType: ActivityConfirmationType
) :Parcelable