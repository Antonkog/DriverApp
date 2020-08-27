package com.redhotapp.driverapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.redhotapp.driverapp.data.local.db.TaskEntity
import com.redhotapp.driverapp.data.model.CommResponseItem

@Dao
interface DriverTaskDao {
    @Query("SELECT * FROM task_entity")
    fun getAll(): LiveData<List<TaskEntity>>

    @Insert
    fun insert(tasks: List<TaskEntity?>?)

    @Delete
    fun delete(user: TaskEntity)

    @Transaction
    fun insertFromCommItem(commonItem: CommResponseItem) {
        if(commonItem.allTask.isNotEmpty()) {
            var strCustList = commonItem.allTask.map { it ->
                TaskEntity(
                    it.taskId, it.status, it.activities.map { it.activityId },
                    it.changeReason, it.address, it.orderDetails, it.palletExchange,
                    false, it.kundenName, it.taskDueDateStart, it.taskDueDateFinish
                )
            }
            insert(strCustList)
        }
    }

}