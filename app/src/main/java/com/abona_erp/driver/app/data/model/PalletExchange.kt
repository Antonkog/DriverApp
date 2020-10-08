package com.abona_erp.driver.app.data.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PalletExchange(
    @SerializedName("ExchangeType")
    val exchangeType: Int,
    @SerializedName("IsDPL")
    val isDPL: Boolean,
    @SerializedName("PalletsAmount")
    val palletsAmount: Int
): Parcelable