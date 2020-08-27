package com.redhotapp.driverapp.data.model


import com.google.gson.annotations.SerializedName

data class Header(
    @SerializedName("DataType")
    val dataType: Int,
    @SerializedName("TimestampSenderUTC")
    val timestampSenderUTC: String,
    @SerializedName("Version")
    val version: Int = 1

)