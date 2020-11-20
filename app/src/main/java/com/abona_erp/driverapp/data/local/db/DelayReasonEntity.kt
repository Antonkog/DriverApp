package com.abona_erp.driverapp.data.local.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abona_erp.driverapp.data.model.DelaySource
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "delay_reason")
@Parcelize
data class DelayReasonEntity(
    @ColumnInfo var mandantId: Int?,
    @ColumnInfo var taskId: Int? = null,
    @ColumnInfo val activityId: Int?,
    @ColumnInfo val translatedReasonText: String?,
    @ColumnInfo val reasonText: String?,
    @ColumnInfo var delaySource: DelaySource = DelaySource.NA,
    @ColumnInfo val waitingReasonType: Int?,
    @ColumnInfo var delayInMinutes: Int = 0,
    @ColumnInfo var timestampUtc: Long = System.currentTimeMillis(),
    @ColumnInfo var comment: String? = null,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
) : Parcelable