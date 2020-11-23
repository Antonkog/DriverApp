package com.abona_erp.driverapp.data.model


import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import java.util.*

data class DelayReasonItem(
    @SerializedName("MandantId")
    val mandantId: Int,
    @SerializedName("TaskId")
    val taskId: Int,
    @SerializedName("ActivityId") //    /** TransportOrderStatusValue.Oid  */
    val activityId: Int,
    @SerializedName("TranslatedReasonText")
    var translatedReasonText: String?,
    @SerializedName("ReasonText")
    var reasonText: String?,
    @SerializedName("DelaySource")
    val delaySource: DelaySource,
    @SerializedName("WaitingReasonId")
    val waitingReasonType: Int,
    @SerializedName("DelayInMinutes")
    val delayInMinutes: Int,
    @SerializedName("TimestampUtc")
    val timestampUtc: String?,
    @SerializedName("Comment")
    val comment: String?
)