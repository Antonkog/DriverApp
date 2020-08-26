package com.redhotapp.driverapp.data.local.db


import com.google.gson.annotations.SerializedName

data class Contact(
    @SerializedName("ContactType")
    val contactType: Int,
    @SerializedName("Name")
    val contactName: String,
    @SerializedName("Number")
    val number: String,
    @SerializedName("NumberType")
    val numberType: Int
)