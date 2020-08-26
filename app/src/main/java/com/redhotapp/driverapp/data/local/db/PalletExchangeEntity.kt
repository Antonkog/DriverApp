package com.redhotapp.driverapp.data.local.db


import com.google.gson.annotations.SerializedName

data class PalletExchangeEntity(
    @SerializedName("ExchangeType")
    val exchangeType: Int,
    @SerializedName("IsDPL")
    val isDPL: Boolean,
    @SerializedName("PalletsAmount")
    val palletsAmount: Int
)