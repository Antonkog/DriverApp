package com.abona_erp.driverapp.data.model


import com.google.gson.annotations.SerializedName

data class PercentItem(
    @SerializedName("MandantId")
    val mandantId: Int,
    @SerializedName("OrderNo")
    val orderNo: Int,
    @SerializedName("PercentFinished")
    val percentFinished: Double,
    @SerializedName("TaskId")
    val taskId: Int,
    @SerializedName("TotalPercentFinished")
    val totalPercentFinished: Double
)