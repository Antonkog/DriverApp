package com.abona_erp.driver.app.data.local.db

import android.os.Parcelable
import androidx.room.*
import com.abona_erp.driver.app.data.model.*
import kotlinx.android.parcel.Parcelize

@Entity (tableName = "task_entity",
    primaryKeys = ["taskId", "mandantId"]
)
@Parcelize
 data class TaskEntity(
    @ColumnInfo  val taskId: Int,
    @ColumnInfo val actionType: ActionType,
    @ColumnInfo val status: TaskStatus,
    val activityIds: List<Int>,
    @ColumnInfo val changeReason: Int,
    @Embedded val address : Address?,
    @ColumnInfo val constacts: List<Contact>?,
    @Embedded val dangerousGoods : DangerousGoods?,
    @Embedded   val orderDetails : OrderDetails?,
    @Embedded val taskDetails : TaskDetails?,
    @Embedded   val palletExchange: PalletExchange?,
    @ColumnInfo val taskDueDateStart: String?,
    @ColumnInfo val taskDueDateFinish: String?,
    @ColumnInfo  val mandantId: Int,
    @ColumnInfo val kundenName: String?,
    @ColumnInfo val notesItem: List<NotesItem>?,
    @ColumnInfo  val confirmationType: ConfirmationType //used for saving ui state - based on user actions
) : Parcelable