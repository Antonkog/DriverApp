package com.abona_erp.driverapp.data.local.dao

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.abona_erp.driverapp.data.local.db.ActivityEntity
import com.abona_erp.driverapp.data.local.db.ConfirmationType
import com.abona_erp.driverapp.data.model.ActivityStatus
import com.abona_erp.driverapp.data.model.CommResponseItem
import com.abona_erp.driverapp.ui.utils.UtilModel.toDelayReasonEntity
import java.util.*

@Dao
interface DriverActDao {
    @Query("SELECT * FROM activity_entity")
    fun getAll(): LiveData<List<ActivityEntity>>

    @Query("SELECT * FROM activity_entity WHERE mandantId =:mandantId AND taskpId =:taskId AND activityId =:actId")
    fun getActivity(actId: Int, taskId: Int, mandantId: Int): ActivityEntity?

    @Query("SELECT * FROM activity_entity")
    fun getAllAsList(): List<ActivityEntity>

    @Query("SELECT * FROM activity_entity WHERE taskpId =:taskId")
    fun getAllByTask(taskId: Int): LiveData<List<ActivityEntity>>

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
    suspend fun update(activity: ActivityEntity) : Int

    @Transaction
    suspend fun updateFromCommItem(commonItem: CommResponseItem) {
        if (commonItem.allTask.isNotEmpty()) {
            val strActList = commonItem.allTask.flatMap {
                it.activities.map {
                    val reasons = it.delayReasons?.map { item -> item.toDelayReasonEntity() }
                    ActivityEntity(
                        it.activityId,
                        it.customActivityId,
                        reasons,
                        it.description,
                        it.finished,
                        it.mandantId,
                        it.name,
                        it.radiusGeoFence,
                        it.sequence,
                        it.taskId,
                        it.started,
                        ActivityStatus.getActivityStatus(it.status),
                        ConfirmationType.RECEIVED
                    )
                }
            }

            val mergedList = arrayListOf<ActivityEntity>()
                //update old activity without status, or insert
            strActList.forEach {
                val oldActivity = getActivity(it.activityId, it.taskpId, it.mandantId)
                if(oldActivity!=null){
                    mergedList.add(it.copy(confirmstatus = oldActivity.confirmstatus, activityStatus = it.activityStatus))

                }
                else mergedList.add(it)
            }

            deleteActivities()
            insert(mergedList)
        }
    }
}