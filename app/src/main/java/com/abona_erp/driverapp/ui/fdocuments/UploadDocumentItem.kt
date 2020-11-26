package com.abona_erp.driverapp.ui.fdocuments

import android.net.Uri
import android.os.Parcelable
import com.abona_erp.driverapp.data.model.DMSDocumentType
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class UploadDocumentItem(
    var uri: Uri,
    var uploaded: Boolean,
    val documentType: DMSDocumentType,
    var createdAt: Date,
    var ModifiedAt: Date
) : Parcelable