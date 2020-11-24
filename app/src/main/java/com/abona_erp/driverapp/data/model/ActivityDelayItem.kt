package com.abona_erp.driverapp.data.model
import com.google.gson.annotations.SerializedName

data class ActivityDelayItem(
    @SerializedName("ActivityId")
    val activityId: Int,
    @SerializedName("DelayReasons")
    val delayReasons: List<DelayReasonItem>?,
    @SerializedName("MandantId")
    val mandantId: Int,
    @SerializedName("TaskId")
    val taskId: Int
)