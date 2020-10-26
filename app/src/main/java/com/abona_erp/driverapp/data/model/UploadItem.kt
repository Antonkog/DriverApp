package com.abona_erp.driverapp.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class UploadItem(
    @SerializedName("Uri")
    val uri: String?,
    @SerializedName("Uploaded")
    val uploaded: Boolean?,
    @SerializedName("DMSDocumentType")
    val documentType: DMSDocumentType?,
    @SerializedName("CreatedAt")
    val createdAt: Date?,
    @SerializedName("ModifiedAt")
    val modifiedAt: Date?
)


