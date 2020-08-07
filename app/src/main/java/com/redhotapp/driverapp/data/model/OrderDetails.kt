package com.redhotapp.driverapp.data.model


import com.google.gson.annotations.SerializedName

data class OrderDetails(
    @SerializedName("CustomerName")
    val customerName: String,
    @SerializedName("CustomerNo")
    val customerNo: Int,
    @SerializedName("OrderNo")
    val orderNo: Int,
    @SerializedName("ReferenceIdCustomer1")
    val referenceIdCustomer1: String,
    @SerializedName("ReferenceIdCustomer2")
    val referenceIdCustomer2: Any
)