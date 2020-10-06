package com.abona_erp.driver.app.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SwapInfoItem {
    @SerializedName("SwapVehicleItem")
    @Expose
    var vehicleItem: VehicleItem? = null

}