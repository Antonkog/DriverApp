package com.abona_erp.driverapp.data.model


import com.google.gson.annotations.SerializedName

data class LatestOrder(
    @SerializedName("action")
    val action: String,
    @SerializedName("eventName")
    val event: String,
    @SerializedName("standing")
    val standing: String,
    @SerializedName("systemid")
    val id: String,
    @SerializedName("unit")
    val unit: String
)