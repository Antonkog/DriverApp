package com.abona_erp.driver.app.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by a.kogan on 9/29/2020
 */
@Entity(tableName = "documents",
    primaryKeys = ["oid", "taskId"]
)
data class DocumentEntity(
//    @ColumnInfo val addedDate: String,
//    @ColumnInfo val addedUser: String,
//    @ColumnInfo val auftragGuid: String,
//    @ColumnInfo val auftragOid: Int,
//    @ColumnInfo val documentType: Int,
    @ColumnInfo val fileName: String,
//    @ColumnInfo val fileType: Int,
//    @ColumnInfo val ladestellenGuid: String,
//    @ColumnInfo val ladestellenOid: Int,
    @ColumnInfo val linkToFile: String,
//    @ColumnInfo val mandantId: Int,
    @ColumnInfo val oid: Int,
    @ColumnInfo val orderNo: Int,
//    @ColumnInfo val sourceReference: Int,/
    @ColumnInfo val taskId: Int,
//    @ColumnInfo val thumbnail: Any,
//    @ColumnInfo val transportAuftragGuid: String,
//    @ColumnInfo val transportAuftragOid: Int,
    @ColumnInfo val vehicleOid: Int
)