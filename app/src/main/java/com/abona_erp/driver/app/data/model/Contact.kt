package com.abona_erp.driver.app.data.model


import com.google.gson.annotations.SerializedName

data class Contact(
    @SerializedName("ContactType")
    val contactType: Int,
    @SerializedName("Name")
    val name: String,
    @SerializedName("Number")
    val number: String,
    @SerializedName("NumberType")
    val numberType: Int
)