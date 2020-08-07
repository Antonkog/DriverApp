package com.redhotapp.driverapp.data.model


import com.google.gson.annotations.SerializedName

data class PalletExchange(
    @SerializedName("ExchangeType")
    val exchangeType: Int,
    @SerializedName("IsDPL")
    val isDPL: Boolean,
    @SerializedName("PalletsAmount")
    val palletsAmount: Int
)