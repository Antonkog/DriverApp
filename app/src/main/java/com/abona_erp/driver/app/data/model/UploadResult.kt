package com.abona_erp.driver.app.data.model

import com.google.gson.annotations.SerializedName

data class UploadResult(
    @SerializedName("FileName")
    var fileName: String?,

    @SerializedName("IsUploadSuccess")
    var isUploadSuccess: Boolean?,

    @SerializedName("IsDownloadSuccess")
    var isDownloadSuccess: Boolean?,

    @SerializedName("ErrorText")
    var errorText: String?
)