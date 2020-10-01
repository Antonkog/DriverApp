package com.abona_erp.driver.app.data.local.db

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.abona_erp.driver.app.data.model.Activity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "activity_entity", indices = arrayOf(Index(value = ["taskpId","mandantId" ])),
    foreignKeys = [
        ForeignKey(entity = TaskEntity::class,
            parentColumns = ["taskId","mandantId"],
            childColumns = ["taskpId","mandantId"],
            onDelete = CASCADE)],
    primaryKeys = ["taskpId", "activityId", "mandantId"]
)

data class ActivityEntity(
    @ColumnInfo val activityId: Int,
    @ColumnInfo val mandantId: Int,
    @ColumnInfo val taskpId: Int,
    @ColumnInfo val started: String?,
    @ColumnInfo val finished: String?,
    @ColumnInfo val name: String?,
    @ColumnInfo  val confirmationType: ConfirmationType
) {
    fun toActivity() :Activity{
        return Activity(activityId,activityId,null,null,null,finished,mandantId,name,0,0,started,0, taskpId)
    }
}
//
//@SerializedName("ActivityId")
//val activityId: Int,
//@SerializedName("CustomActivityId")
//val customActivityId: Int,
//@SerializedName("DelayReasons")
//val delayReasons: Any,
//@SerializedName("Description")
//val description: String?,
//@SerializedName("DeviceId")
//val deviceId: String?,
//@SerializedName("Finished")
//val finished: String?,
//@SerializedName("MandantId")
//val mandantId: Int,
//@SerializedName("Name")
//val name: String?,
//@SerializedName("RadiusGeoFence")
//val radiusGeoFence: Int,
//@SerializedName("Sequence")
//val sequence: Int,
//@SerializedName("Started")
//val started: String?,
//@SerializedName("Status")
//val status: Int,
//@SerializedName("TaskId")