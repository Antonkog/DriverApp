package com.abona_erp.driverapp.data.model


import com.google.gson.annotations.SerializedName

data class CommItem(
    @SerializedName("Header")
    val header: Header,
    @SerializedName("CompressedTasksCount")
    val compressedTasksCount: Int? = null,
    @SerializedName("PercentItem")
    val percentItem: PercentItem? = null,
    @SerializedName("TaskItem")
    val taskItem: TaskItem? = null,
    @SerializedName("ActivityItem")
    val activityItem: Activity? = null,
    @SerializedName("VehicleItem")
    val vehicleItem: VehicleItem? = null,
    @SerializedName("DeviceProfileItem")
    val deviceProfileItem: DeviceProfileItem? = null,
    @SerializedName("ConfirmationItem")
    var confirmationItem: ConfirmationItem? = null,
    @SerializedName("DocumentItem")
    val documentItem: DocumentItem? = null,
    @SerializedName("DelayReasons")
    val delayReasonItems: List<DelayReasonItem>? = null

) {
    data class Builder(
        var header: Header,
        var compressedTasksCount: Int? = null,
        var percentItem: PercentItem? = null,
        var taskItem: TaskItem? = null,
        var activityItem: Activity? = null,
        var vehicleItem: VehicleItem? = null,
        var deviceProfileItem: DeviceProfileItem? = null,
        var confirmationItem: ConfirmationItem? = null,
        var documentItem: DocumentItem?  = null,
        var delayReasonItems: List<DelayReasonItem>? = null
    ) {
        fun activityItem(activity: Activity) = apply { this.activityItem = activity }
        fun deviceProfileItem(device: DeviceProfileItem) = apply { this.deviceProfileItem = device }
        fun confirmationItem(confirmationItem: ConfirmationItem) = apply { this.confirmationItem = confirmationItem }
        fun delayReasonItems(delayReasonItems: List<DelayReasonItem>) = apply { this.delayReasonItems = delayReasonItems }

        fun build() = CommItem(
            header,
            compressedTasksCount,
            percentItem,
            taskItem,
            activityItem,
            vehicleItem,
            deviceProfileItem,
            confirmationItem,
            documentItem,
            delayReasonItems
        )
    }
}