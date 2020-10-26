package com.abona_erp.driverapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VehicleItem {
    // ------------------------------------------------------------------------
    // GETTER & SETTER
    @SerializedName("MandantId")
    @Expose
    var mandantId: Int? = null

    @SerializedName("ClientName")
    @Expose
    var clientName: String? = null

    @SerializedName("RegistrationNumber")
    @Expose
    var registrationNumber: String? = null

    @SerializedName("Drivers")
    @Expose
    var drivers: List<DriverItem>? = null

}