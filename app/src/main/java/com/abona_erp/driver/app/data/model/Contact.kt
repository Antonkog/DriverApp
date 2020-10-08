package com.abona_erp.driver.app.data.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact(
    @SerializedName("ContactType")
    val contactType: Int,
    @SerializedName("Name")
    val name: String,
    @SerializedName("Number")
    val number: String,
    @SerializedName("NumberType")
    val numberType: Int
): Parcelable