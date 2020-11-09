package com.abona_erp.driverapp.data.model


import com.abona_erp.driverapp.ui.utils.UtilModel
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

) {
    data class Builder(
        var dataType: Int,
        var deviceId: String
    ) {
        fun build() = Header(dataType, UtilModel.getCurrentDateServerFormat(), deviceId)
    }
}