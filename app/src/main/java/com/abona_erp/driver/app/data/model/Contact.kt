package com.abona_erp.driver.app.data.model


import com.google.gson.annotations.SerializedName

data class Contact(
    @SerializedName("ContactType")
    val contactType: Int,
    @SerializedName("Name")
    val name: Any,
    @SerializedName("Number")
    val number: Any,
    @SerializedName("NumberType")
    val numberType: Int
)