package com.abona_erp.driver.app.data.local.db
import com.google.gson.annotations.SerializedName

enum class ActionType(val typeCode: Int) {
    @SerializedName("0") PICK_UP(0),
    @SerializedName("1") DROP_OFF(1),
    @SerializedName("2") GENERAL(2),
    @SerializedName("3") TRACTOR_SWAP(3),
    @SerializedName("4") DELAY(4),
    @SerializedName("100") UNKNOWN(100),
    @SerializedName("-1") ENUM_ERROR(-1);


    companion object {
        fun getActionType(typeCode: Int): ActionType {
            for (lt in values()) {
                if (lt.typeCode == typeCode) {
                    return lt
                }
            }
            return ENUM_ERROR
        }
    }
}

