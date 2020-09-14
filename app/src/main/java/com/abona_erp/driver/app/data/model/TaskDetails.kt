package com.abona_erp.driver.app.data.model


import com.google.gson.annotations.SerializedName

data class TaskDetails(
    @SerializedName("Description")
    val description: Any,
    @SerializedName("LoadingOrder")
    val loadingOrder: Int,
    @SerializedName("ReferenceId1")
    val referenceId1: Any,
    @SerializedName("ReferenceId2")
    val referenceId2: Any
)