package com.redhotapp.driverapp.data.model


import com.google.gson.annotations.SerializedName

data class CommResponceItem(
    @SerializedName("AllAppFileInterchangeItem")
    val allAppFileInterchangeItem: Any,
    @SerializedName("AllDocumentCommItem")
    val allDocumentCommItem: List<Any>,
    @SerializedName("AllTask")
    val allTask: List<AllTask>,
    @SerializedName("AllTaskCommItem")
    val allTaskCommItem: List<Any>,
    @SerializedName("CommunicationItem")
    val communicationItem: Any,
    @SerializedName("DelayReasons")
    val delayReasons: Any,
    @SerializedName("ExceptionText")
    val exceptionText: Any,
    @SerializedName("HttpResponseMessage")
    val httpResponseMessage: Any,
    @SerializedName("IsException")
    val isException: Boolean,
    @SerializedName("IsSuccess")
    val isSuccess: Boolean,
    @SerializedName("LogText")
    val logText: Any,
    @SerializedName("Text")
    val text: String,
    @SerializedName("TransportAuftragOid")
    val transportAuftragOid: Int
)