package com.abona_erp.driverapp.data.model

import com.google.gson.annotations.SerializedName

data class DeviceProfileItem(
    @SerializedName("Model")
    var model: String? = "",

    @SerializedName("Manufacturer")
    var manufacturer: String? = "",

    @SerializedName("DeviceId")
    var deviceId: String? = "",

    @SerializedName("Serial")
    var serial: String? = null,

    @SerializedName("InstanceId")
    var instanceId: String? = "",

    @SerializedName("CreatedDate")
    var createdDate: String? = "",

    @SerializedName("UpdatedDate")
    var updatedDate: String? = "",

    @SerializedName("LanguageCode")
    var languageCode: String? = "",
    @SerializedName("VersionCode")
    var versionCode: Int = 0,
    @SerializedName("VersionName")
    var versionName: String = ""
)