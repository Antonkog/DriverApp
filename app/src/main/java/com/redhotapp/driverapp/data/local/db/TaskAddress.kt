package com.redhotapp.driverapp.data.local.db


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.google.gson.annotations.SerializedName

data class TaskAddress(
    @SerializedName("City")
    val city: String,
    @SerializedName("Latitude")
    val latitude: Double,
    @SerializedName("Longitude")
    val longitude: Double,
    @SerializedName("Name1")
    val name1: String,
    @SerializedName("Name2")
    val name2: String,
    @SerializedName("Nation")
    val nation: String,
    @SerializedName("Note")
    val note: String,
    @SerializedName("State")
    val state: String,
    @SerializedName("Street")
    val street: String,
    @SerializedName("ZIP")
    val zip: String
)