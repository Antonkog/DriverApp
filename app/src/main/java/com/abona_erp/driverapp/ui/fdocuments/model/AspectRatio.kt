package com.abona_erp.driverapp.ui.fdocuments.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AspectRatio(
    val aspectRatioTitle: String?,
    val aspectRatioX: Float,
    val aspectRatioY: Float
) : Parcelable