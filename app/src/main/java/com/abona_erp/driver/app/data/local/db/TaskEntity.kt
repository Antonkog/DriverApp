package com.abona_erp.driver.app.data.local.db

import androidx.room.*
import com.abona_erp.driver.app.data.model.Address
import com.abona_erp.driver.app.data.model.Contact
import com.abona_erp.driver.app.data.model.OrderDetails
import com.abona_erp.driver.app.data.model.PalletExchange

@Entity (tableName = "task_entity")
data class TaskEntity(
    @PrimaryKey  val taskId: Int,
    @ColumnInfo val actionType: Int,
    val activityIds: List<Int>,
    @ColumnInfo val changeReason: Int,
    @Embedded val address : Address?,
//    @TypeConverters(GsonToDbConverter::class) val constacts: List<Contact>?,
//    @TypeConverters(GsonToDbConverter::class) val dangerousGoods : List<DangerousGoods>?,
    @Embedded   val orderDetails : OrderDetails?,
    @Embedded   val palletExchange: PalletExchange?,
    @ColumnInfo val finished: Boolean,
    @ColumnInfo val taskDueDateStart: String?,
    @ColumnInfo val taskDueDateFinish: String?,
    @ColumnInfo val kundenName: String?
)
/*
TaskEntity(taskId=5956,
 actionType=0,
  activityIds=[15, 16, 17],
   changeReason=1,
    address=Address(city=Kharkiv, latitude=0.0, longitude=0.0, name1=Kharkiv, name2=null, nation=UA, note=null, state=null, street=null, zIP=null), orderDetails=OrderDetails(customerName=ANTON KOGAN TEST3, customerNo=60213, orderNo=202038131, referenceIdCustomer1=32ereqrq, referenceIdCustomer2=null), palletExchange=PalletExchange(exchangeType=0, isDPL=false, palletsAmount=-1), finished=false, taskDueDateStart=0001-01-01T00:00:00, taskDueDateFinish=2020-09-18T00:00:00, kundenName=ANTON KOGAN TEST3)
 */