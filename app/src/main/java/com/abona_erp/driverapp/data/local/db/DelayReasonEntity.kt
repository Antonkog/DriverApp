package com.abona_erp.driverapp.data.local.db

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.abona_erp.driverapp.data.model.DelaySource
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "delay_reason")
@Parcelize
data class DelayReasonEntity(
    @ColumnInfo val waitingReasonType: Int?,
    @ColumnInfo val activityId: Int?,
    @ColumnInfo val reasonText: String?,
    @ColumnInfo val translatedReasonText: String?,
    @ColumnInfo val code: Int?,
    @ColumnInfo val subcode: Int?,
    @ColumnInfo val mandantId: Int?,
    @ColumnInfo val taskId: Int?,
    @ColumnInfo val timestampUtc: Long,
    @ColumnInfo val delayInMinutes: Int?,
    @ColumnInfo val delaySource: DelaySource?,
    @ColumnInfo val comment: String?,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
) : Parcelable