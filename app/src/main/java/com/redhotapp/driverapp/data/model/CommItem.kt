package com.redhotapp.driverapp.data.model


import com.google.gson.annotations.SerializedName

data class  CommItem @JvmOverloads constructor(
    @SerializedName("CompressedTasksCount")
    val compressedTasksCount: Int? = null,
    @SerializedName("Header")
    val header: Header,
    @SerializedName("PercentItem")
    var percentItem: PercentItem? = null,
    @SerializedName("TaskItem")
    var taskItem: TaskItem? = null,
    @SerializedName("ActivityItem")
    var activityItem: Activity? = null,
    @SerializedName("DeviceProfileItem")
    var deviceProfileItem: DeviceProfileItem? = null
)