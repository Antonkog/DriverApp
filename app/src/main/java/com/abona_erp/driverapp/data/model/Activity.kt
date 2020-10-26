package com.abona_erp.driverapp.data.model


import com.google.gson.annotations.SerializedName

data class Activity(
    @SerializedName("ActivityId")
    val activityId: Int,
    @SerializedName("CustomActivityId")
    val customActivityId: Int,
    @SerializedName("DelayReasons")
    val delayReasons: List<DelayReasonItem>?,
    @SerializedName("Description")
    val description: String?,
    @SerializedName("DeviceId")
    val deviceId: String?,
    @SerializedName("Finished")
    val finished: String?,
    @SerializedName("MandantId")
    val mandantId: Int,
    @SerializedName("Name")
    val name: String?,
    @SerializedName("RadiusGeoFence")
    val radiusGeoFence: Int,
    @SerializedName("Sequence")
    val sequence: Int,
    @SerializedName("Started")
    val started: String?,
    @SerializedName("Status")
    val status: Int,
    @SerializedName("TaskId")
    val taskId: Int
)