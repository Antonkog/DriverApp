package com.redhotapp.driverapp.data.model


import com.google.gson.annotations.SerializedName

data class CommItem(
    @SerializedName("CompressedTasksCount")
    val compressedTasksCount: Int,
    @SerializedName("Header")
    val header: Header,
    @SerializedName("PercentItem")
    val percentItem: PercentItem,
    @SerializedName("TaskItem")
    val taskItem: TaskItem
)