package com.abona_erp.driverapp.data.local.dao

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.abona_erp.driverapp.data.local.db.ActionType
import com.abona_erp.driverapp.data.local.db.ActivityConfirmationType
import com.abona_erp.driverapp.data.local.db.ConfirmationType
import com.abona_erp.driverapp.data.local.db.TaskEntity
import com.abona_erp.driverapp.data.model.ActivityStatus
import com.abona_erp.driverapp.data.model.CommResponseItem
import com.abona_erp.driverapp.ui.ftasks.TaskWithActivities

@Dao
interface DriverTaskDao {
    @Query("SELECT * FROM task_entity")
    fun observeTasks(): LiveData<List<TaskEntity>>

    /**
     * Select all tasks from the task_entity table.
     *
     * @return all tasks.
     */
    @Query("SELECT * FROM task_entity")
    suspend fun getTasks(): List<TaskEntity>



    @Query("SELECT * FROM task_entity WHERE taskId =:taskpid AND mandantId =:mandantId")
    suspend fun getTaskByIds(taskpid: Int, mandantId: Int): TaskEntity?

    @Query("SELECT * FROM task_entity WHERE orderNo =:orderNo AND taskId =:taskpid AND mandantId =:mandantId")
    suspend fun getTaskByOrder(orderNo: Int, taskpid: Int, mandantId: Int): TaskEntity?

    @Update
    suspend fun update(taskEntity: TaskEntity): Int

    /**
     * Delete all tasks.
     */
    @Query("DELETE FROM task_entity")
    suspend fun deleteTasks()

    @Insert
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(tasks: TaskEntity)


    @Delete
    fun delete(user: TaskEntity)

    @Transaction
    @Query("SELECT * FROM task_entity")
    fun observeTaskWithActivities(): LiveData<List<TaskWithActivities>>

    @Transaction
    suspend fun updateFromCommItem(commonItem: CommResponseItem) {
        if (commonItem.allTask.isNotEmpty()) {
            val strCustList = commonItem.allTask.map { it ->

                var status = getStatus(it.taskId)
                if(status == ConfirmationType.RECEIVED){// we dont set confirm type on server and loose it when log-out
                    val activeTasks = it.activities.filter{it.status < ActivityStatus.FINISHED.status}
                    if(activeTasks.isEmpty())
                        status = ConfirmationType.TASK_CONFIRMED_BY_USER // we change task as confirmed if all activity set
                }

                TaskEntity(
                    it.taskId,
                    ActionType.getActionType(it.actionType),
                    it.status,
                    it.activities.map { it.activityId },
                    it.changeReason,
                    it.address,
                    it.contacts,
                    it.dangerousGoods,
                    it.orderDetails,
                    it.taskDetails,
                    it.palletExchange,
                    it.taskDueDateStart,
                    it.taskDueDateFinish,
                    it.mandantId,
                    it.kundenName,
                    it.notes,
                    status,
                    false
                )//todo: check if make sense not to override confirmation type from server.
            }
            Log.d("DriverTaskDao", "insert taskEntity $strCustList  \nsize: " + strCustList.size)
            insertTasks(strCustList)
        }
    }

    private suspend fun getStatus(taskid: Int): ConfirmationType? {
        getTasks().forEach { 
            if(it.taskId == taskid){
                return it.confirmationType
            }
        }
        return ConfirmationType.RECEIVED
    }
}