package com.abona_erp.driverapp.data.model


import com.google.gson.annotations.SerializedName

data class DocumentResponse(
    @SerializedName("AddedDate")
    val addedDate: String,
    @SerializedName("AddedUser")
    val addedUser: String,
    @SerializedName("AuftragGuid")
    val auftragGuid: String,
    @SerializedName("AuftragOid")
    val auftragOid: Int,
    @SerializedName("DocumentType")
    val documentType: Int,
    @SerializedName("FileName")
    val fileName: String,
    @SerializedName("FileType")
    val fileType: Int,
    @SerializedName("LadestellenGuid")
    val ladestellenGuid: String,
    @SerializedName("LadestellenOid")
    val ladestellenOid: Int,
    @SerializedName("LinkToFile")
    val linkToFile: String,
    @SerializedName("MandantId")
    val mandantId: Int,
    @SerializedName("Oid")
    val oid: Int,
    @SerializedName("OrderNo")
    val orderNo: Int,
    @SerializedName("SourceReference")
    val sourceReference: Int,
    @SerializedName("TaskId")
    val taskId: Int,
    @SerializedName("Thumbnail")
    val thumbnail: Any,
    @SerializedName("TransportAuftragGuid")
    val transportAuftragGuid: String,
    @SerializedName("TransportAuftragOid")
    val transportAuftragOid: Int,
    @SerializedName("VehicleOid")
    val vehicleOid: Int
)