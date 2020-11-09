package com.abona_erp.driverapp.data.local.db

import com.google.gson.annotations.SerializedName

enum class LogType(val typeCode: Int) {
    @SerializedName("0")
    FCM(0),
    @SerializedName("1")
    APP_TO_SERVER(1),
    @SerializedName("-1")
    ENUM_ERROR(-1);


    companion object {
        fun getLogType(typeCode: Int): LogType {
            for (lt in values()) {
                if (lt.typeCode == typeCode) {
                    return lt
                }
            }
            return ENUM_ERROR
        }
    }
}

