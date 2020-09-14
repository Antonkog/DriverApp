package com.abona_erp.driver.app.data.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Header(
    @SerializedName("DataType")
    val dataType: Int,
    @SerializedName("TimestampSenderUTC")
    val timestampSenderUTC: String,
    @SerializedName("DeviceId")
    private val deviceId: String,
    @SerializedName("Version")
    val version: Int = 1
)