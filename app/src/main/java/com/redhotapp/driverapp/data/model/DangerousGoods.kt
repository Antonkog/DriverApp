package com.redhotapp.driverapp.data.model


import com.google.gson.annotations.SerializedName

data class DangerousGoods(
    @SerializedName("ADRClass")
    val aDRClass: Any,
    @SerializedName("DangerousGoodsClassType")
    val dangerousGoodsClassType: Int,
    @SerializedName("IsGoodsDangerous")
    val isGoodsDangerous: Boolean,
    @SerializedName("UNNo")
    val uNNo: Any
)