package com.abona_erp.driverapp.data.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DangerousGoods(
    @SerializedName("ADRClass")
    val aDRClass: Int,
    @SerializedName("DangerousGoodsClassType")
    val dangerousGoodsClassType: Int,
    @SerializedName("IsGoodsDangerous")
    val isGoodsDangerous: Boolean,
    @SerializedName("UNNo")
    val uNNo: String?
) : Parcelable