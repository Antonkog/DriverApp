package com.abona_erp.driver.app.data.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderDetails(
    @SerializedName("CustomerName")
    val customerName: String?,
    @SerializedName("CustomerNo")
    val customerNo: Int,
    @SerializedName("OrderNo")
    val orderNo: Int,
    @SerializedName("ReferenceIdCustomer1")
    val referenceIdCustomer1: String?,
    @SerializedName("ReferenceIdCustomer2")
    val referenceIdCustomer2: String?
) : Parcelable