package com.abona_erp.driver.app.data.model


import com.google.gson.annotations.SerializedName

class  CommItem(
    @SerializedName("CompressedTasksCount")
    val compressedTasksCount: Int? = null,
    @SerializedName("Header")
    val header: Header,
    @SerializedName("PercentItem")
    val percentItem: PercentItem? = null,
    @SerializedName("TaskItem")
    val taskItem: TaskItem? = null,
    @SerializedName("ActivityItem")
    val activityItem: Activity? = null,
    @SerializedName("DeviceProfileItem")
    val deviceProfileItem: DeviceProfileItem? = null){
    data class Builder(
                        var compressedTasksCount: Int? = null,
                        var header: Header,
                        var percentItem: PercentItem? = null,
                        var taskItem: TaskItem? = null,
                        var activityItem: Activity? = null,
                        var deviceProfileItem: DeviceProfileItem? = null){
        fun compressedTasksCount(count: Int?) = apply { this.compressedTasksCount = count }
        fun header(headerb: Header) = apply { this.header = headerb }
        fun percentItem(percent: PercentItem?) = apply { this.percentItem = percent }
        fun taskItem(task: TaskItem?) = apply { this.taskItem = task }
        fun activityItem(activity: Activity?) = apply { this.activityItem = activity }
        fun deviceProfileItem(device: DeviceProfileItem?) = apply { this.deviceProfileItem = device }
        fun build() = CommItem(compressedTasksCount, header, percentItem, taskItem, activityItem, deviceProfileItem)
    }
}