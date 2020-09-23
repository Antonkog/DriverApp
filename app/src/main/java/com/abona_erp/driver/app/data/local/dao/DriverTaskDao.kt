package com.abona_erp.driver.app.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abona_erp.driver.app.data.local.db.ActivityEntity
import com.abona_erp.driver.app.data.local.db.ConfirmationType
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.abona_erp.driver.app.data.model.CommResponseItem

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

    /**
     * Delete all tasks.
     */
    @Query("DELETE FROM task_entity")
    suspend fun deleteTasks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(tasks: List<TaskEntity?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(tasks: TaskEntity)


    @Delete
    fun delete(user: TaskEntity)

    @Transaction
    suspend fun insertFromCommItem(commonItem: CommResponseItem) {
        if(commonItem.allTask.isNotEmpty()) {
            var strCustList = commonItem.allTask.map { it ->
                TaskEntity(
                    it.taskId, it.actionType, it.status, it.activities.map { it.activityId },
                    it.changeReason, it.address, it.orderDetails, it.palletExchange,
                    false,  it.taskDueDateStart, it.taskDueDateFinish, it.mandantId, it.kundenName,
                            ConfirmationType.RECEIVED)//todo: check if make sense not to override confirmation type from server.
            }
            insertOrReplace(strCustList)
        }
    }
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(activityEntity: ActivityEntity)
}