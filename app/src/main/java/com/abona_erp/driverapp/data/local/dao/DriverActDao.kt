package com.abona_erp.driverapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abona_erp.driverapp.data.local.db.ActivityEntity
import com.abona_erp.driverapp.data.model.CommResponseItem
import com.abona_erp.driverapp.ui.utils.UtilModel.toActivityEntity

@Dao
interface DriverActDao {
    @Query("SELECT * FROM activity_entity ORDER BY sequence ASC")
    fun getAll(): LiveData<List<ActivityEntity>>

    @Query("SELECT * FROM activity_entity WHERE mandantId =:mandantId AND taskpId =:taskId AND activityId =:actId")
    fun getActivity(actId: Int, taskId: Int, mandantId: Int): ActivityEntity?

    @Query("SELECT * FROM activity_entity  ORDER BY sequence ASC")
    fun getAllAsList(): List<ActivityEntity>

    @Query("SELECT * FROM activity_entity WHERE taskpId =:taskId  ORDER BY sequence ASC")
    fun getAllByTask(taskId: Int): LiveData<List<ActivityEntity>>

    @Query("SELECT * FROM activity_entity WHERE taskpId =:taskId  ORDER BY sequence ASC LIMIT 1")
    fun getFirstTaskAct(taskId: Int): ActivityEntity?

    /**
     * Delete all Activities.
     */
    @Query("DELETE FROM activity_entity")
    suspend fun deleteActivities()

    @Insert
    suspend fun insert(activities: List<ActivityEntity>)

    @Insert
    suspend fun insert(activity: ActivityEntity)

    @Update
    suspend fun update(activity: ActivityEntity): Int

    @Transaction
    suspend fun updateFromCommItem(commonItem: CommResponseItem) {
        if (commonItem.allTask.isNotEmpty()) {
            val strActList = commonItem.allTask.flatMap {
                it.activities.map { act -> act.toActivityEntity() }
            }

            insert(strActList) // server always wins, -means activity status can be modified on server side and should be replaced.
        }
    }
}