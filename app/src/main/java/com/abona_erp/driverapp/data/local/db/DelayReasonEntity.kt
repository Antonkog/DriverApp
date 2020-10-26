package com.abona_erp.driverapp.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.abona_erp.driverapp.data.model.DelaySource
import java.util.*

@Entity(
    tableName = "delay_reason",
    indices = arrayOf(Index(value = ["taskpId", "mandantId", "activityId"]))
)
data class DelayReasonEntity(
    @PrimaryKey val waitingReasongId: Int?,
    @ColumnInfo val activityId: Int?,
    @ColumnInfo val reasonText: String?,
    @ColumnInfo val translatedReasonText: String?,
    @ColumnInfo val code: Int?,
    @ColumnInfo val subcode: Int?,
    @ColumnInfo val mandantId: Int?,
    @ColumnInfo val taskId: Int?,
    @ColumnInfo val timestampUtc: Date?,
    @ColumnInfo val delayInMinutes: Int?,
    @ColumnInfo val delaySource: DelaySource?,
    @ColumnInfo val comment: String?
)