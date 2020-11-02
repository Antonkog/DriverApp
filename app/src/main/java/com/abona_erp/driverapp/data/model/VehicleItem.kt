package com.abona_erp.driverapp.data.model

import com.google.gson.annotations.SerializedName

data class VehicleItem(
    @SerializedName("MandantId")
    var mandantId: Int?,
    @SerializedName("ClientName")
    var clientName: String?,
    @SerializedName("RegistrationNumber")
    var registrationNumber: String?,
    @SerializedName("Drivers")
    var drivers: List<DriverItem>?
)