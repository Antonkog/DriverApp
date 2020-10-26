package com.abona_erp.driverapp.data.model


import com.abona_erp.driverapp.data.Constant
import com.google.gson.annotations.SerializedName
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class Header(
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
        fun build() = Header(dataType, getTimeStamp(), deviceId)
        private fun getTimeStamp(): String {
            val dfUtc: DateFormat = SimpleDateFormat(Constant.abonaDateFormat, Locale.getDefault())
            dfUtc.timeZone = TimeZone.getTimeZone(Constant.abonaTimeZone)
            return dfUtc.format(Date())
        }
    }
}