package com.abona_erp.driver.app.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DriverItem (
    @SerializedName("LastnameFirstname")
    val lastnameFirstname: String?,

    @SerializedName("DriverNo")
    @Expose
    var driverNo: Int,

    @SerializedName("ImageUrl")
    @Expose
    var imageUrl: String?,

    @SerializedName("Sms")
    @Expose
    var sms: String?

)