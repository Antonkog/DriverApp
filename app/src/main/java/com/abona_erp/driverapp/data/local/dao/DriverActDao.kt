package com.abona_erp.driverapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abona_erp.driverapp.data.local.db.ActivityEntity
import com.abona_erp.driverapp.data.local.db.ConfirmationType
import com.abona_erp.driverapp.data.model.ActivityStatus
import com.abona_erp.driverapp.data.model.CommResponseItem
import com.abona_erp.driverapp.ui.utils.UtilModel.toDelayReasonEntity

@Dao
interface DriverActDao {
    @Query("SELECT * FROM activity_entity")
    fun getAll(): LiveData<List<ActivityEntity>>

    @Query("SELECT * FROM activity_entity WHERE mandantId =:mandantId AND taskpId =:taskId AND activityId =:actId")
    fun getActivity(actId: Int, taskId: Int, mandantId: Int): ActivityEntity

    @Query("SELECT * FROM activity_entity WHERE taskpId =:taskId")
    fun getAllByTask(taskId: Int): LiveData<List<ActivityEntity>>

    @Delete
    fun delete(activity: ActivityEntity)

    @Delete
    fun deleteAll(activitys: List<ActivityEntity>)


    /**
     * Delete all Activities.
     */
    @Query("DELETE FROM activity_entity")
    suspend fun deleteActivities()

    @Insert
    suspend fun insert(activities: List<ActivityEntity>)

    @Update
    suspend fun update(activity: ActivityEntity) : Int

    @Transaction
    suspend fun insertFromCommItem(commonItem: CommResponseItem) {
        if (commonItem.allTask.isNotEmpty()) {
            var strActList = commonItem.allTask.flatMap {
                it.activities.map {
                    //ActivityEntity(it.activityId, it.mandantId, it.taskId, it.started, it.finished, it.name, ConfirmationType.RECEIVED) //todo: check if make sense not to override confirmation type from server.
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
            insert(strActList)
        }
    }
}