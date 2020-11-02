package com.abona_erp.driverapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DriverItem(
    @SerializedName("LastnameFirstname")
    val lastnameFirstname: String?,

    @SerializedName("DriverNo")
    var driverNo: Int,

    @SerializedName("ImageUrl")
    var imageUrl: String?,

    @SerializedName("Sms")
    var sms: String?
)