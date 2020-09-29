package com.abona_erp.driver.app.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

data class UploadItem (
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


