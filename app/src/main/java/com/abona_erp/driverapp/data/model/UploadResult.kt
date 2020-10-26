package com.abona_erp.driverapp.data.model

import com.google.gson.annotations.SerializedName

data class UploadResult(
    @SerializedName("FileName")
    var fileName: String?,

    @SerializedName("IsSuccess")
    var isUploadSuccess: Boolean?,

    @SerializedName("IsException")
    var isDownloadSuccess: Boolean?,

    @SerializedName("ErrorText")
    var errorText: String?
)