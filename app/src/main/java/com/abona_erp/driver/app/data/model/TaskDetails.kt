package com.abona_erp.driver.app.data.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TaskDetails(
    @SerializedName("Description")
    val description: String?,
    @SerializedName("LoadingOrder")
    val loadingOrder: Int,
    @SerializedName("ReferenceId1")
    val referenceId1: String?,
    @SerializedName("ReferenceId2")
    val referenceId2: String?
) : Parcelable