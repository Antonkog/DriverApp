/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.abona_erp.driverapp.data.local

import androidx.lifecycle.LiveData
import com.abona_erp.driverapp.data.ResultWithStatus
import com.abona_erp.driverapp.data.local.db.ActivityEntity
import com.abona_erp.driverapp.data.local.db.AppDatabase
import com.abona_erp.driverapp.data.local.db.DocumentEntity
import com.abona_erp.driverapp.data.local.db.TaskEntity
import com.abona_erp.driverapp.data.model.CommResponseItem
import com.abona_erp.driverapp.data.model.DocumentResponse
import com.abona_erp.driverapp.ui.ftasks.TaskWithActivities
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of a data source as a db.
 */
class LocalDataSource internal constructor(
    private val db: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun observeActivities(taskId: Int): LiveData<List<ActivityEntity>> {
        return db.driverActDao().getAllByTask(taskId)
    }

    fun observeAllActivities(): LiveData<List<ActivityEntity>> {
        return db.driverActDao().getAll()
    }

    fun observeTasksWithActivities(): LiveData<List<TaskWithActivities>> {
        return db.driverTaskDao().observeTaskWithActivities()
    }

    fun observeTasks(): LiveData<List<TaskEntity>> {
        return db.driverTaskDao().observeTasks()
    }

    suspend fun getTasks(): ResultWithStatus<List<TaskEntity>> = withContext(ioDispatcher) {
        return@withContext try {
            ResultWithStatus.Success(db.driverTaskDao().getTasks())
        } catch (e: Exception) {
            ResultWithStatus.Error(e)
        }
    }

    suspend fun insertOrReplaceTask(taskEntity: TaskEntity) {
        db.driverTaskDao().insertOrReplace(taskEntity)
    }

    suspend fun insertOrUpdateActivity(activityEntity: ActivityEntity) {
        val old = db.driverActDao().getActivity(activityEntity.activityId, activityEntity.taskpId, activityEntity.mandantId)
        if( old!= null){
            db.driverActDao().update(activityEntity.copy(activityStatus =  old.activityStatus, confirmstatus = old.confirmstatus ))
        }
        else  db.driverActDao().insert(activityEntity)
    }


    suspend fun updateFromCommItem(data: CommResponseItem) {
        db.driverTaskDao().updateFromCommItem(data)
        db.driverActDao().updateFromCommItem(data)
    }

    fun observeDocuments(): LiveData<List<DocumentEntity>> {
        return db.documentsDao().observeDocuments()
    }

    suspend fun getDocuments(): ResultWithStatus<List<DocumentEntity>> = withContext(ioDispatcher) {
        return@withContext try {
            ResultWithStatus.Success(db.documentsDao().getDocuments())
        } catch (e: Exception) {
            ResultWithStatus.Error(e)
        }
    }

    suspend fun insertDocument(documentEntity: DocumentEntity) {
        db.documentsDao().insertOrReplace(documentEntity)
    }

    suspend fun insertDocumentResponse(responseItems: List<DocumentResponse>) {
        responseItems.map {
            DocumentEntity(it.fileName, it.linkToFile, it.oid, it.orderNo, it.taskId, it.vehicleOid)
        }.let {
            db.documentsDao().insertOrReplace(it)
        }
    }

    suspend fun deleteDocuments() {
        db.documentsDao().deleteDocuments()
    }

    suspend fun updateActivity(activityEntity: ActivityEntity): Int {
        return db.driverActDao().update(activityEntity)
    }

    suspend fun updateTask(taskEntity: TaskEntity): Int {
        return db.driverTaskDao().update(taskEntity)
    }

    fun getNextActivityIfExist(activityEntity: ActivityEntity): ActivityEntity {
      return  db.driverActDao().getActivity(activityEntity.activityId+1, activityEntity.taskpId, activityEntity.mandantId)
    }

    suspend fun getParentTask(activityEntity: ActivityEntity): TaskEntity {
       return db.driverTaskDao().getParentTask(activityEntity.taskpId, activityEntity.mandantId)
    }

    suspend fun cleanDatabase() { //todo: clean data, check what with foreign keys
//        db.driverTaskDao().deleteTasks()
//        db.driverActDao().deleteActivities()
//        db.documentsDao().deleteDocuments()
    }

}
