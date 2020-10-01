package com.abona_erp.driver.app.data.model

import com.google.gson.annotations.Expose
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