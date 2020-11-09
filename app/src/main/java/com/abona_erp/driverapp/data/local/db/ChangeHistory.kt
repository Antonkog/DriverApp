package com.abona_erp.driverapp.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Anton Kogan email: Akogan777@gmail.com on 11/9/2020
 */


@Entity(tableName = "change_history")
data class ChangeHistory(
    @ColumnInfo val status: Status,
    @ColumnInfo val logType: LogType,
    @ColumnInfo val dataType :HistoryDataType,
    @ColumnInfo val params : String,
    @ColumnInfo val response : String?,
    @ColumnInfo val created: Long,
    @ColumnInfo val modified: Long,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
)
enum class Status{SENT, SUCCESS, ERROR} //that is for restApi FCM is always SUCCESS, as we got it.
data class RequestParamsHolder(val taskId: Int?, val actId: Int?, val orderNum : Int?, val mandantId: Int?, val filePath: String?) //used to hold params as string, if CommItem Used, we write it to params
enum class HistoryDataType{
    FCM_TASK,
    FCM_DOCUMENT,
    FCM_VEHICLE,

    AUTH,
    SET_DEVICE_PROFILE,
    GET_TASKS,
    GET_DOCUMENTS,
    POST_ACTIVITY,
    CONFIRM_TASK,
    UPLOAD_DOCUMENT
}


//public String getCsvHeader() {
//    return "id,title,message,direction,action_type,state,created_at,modified_at,task_id,activity_id,order_number,mandant_id\n";
//}