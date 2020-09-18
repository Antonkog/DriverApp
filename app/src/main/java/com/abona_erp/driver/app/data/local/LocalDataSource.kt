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
package com.abona_erp.driver.app.data.local

import androidx.lifecycle.LiveData
import com.abona_erp.driver.app.data.ResultWithStatus
import com.abona_erp.driver.app.data.local.db.ActivityEntity
import com.abona_erp.driver.app.data.local.db.AppDatabase
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.abona_erp.driver.app.data.model.CommResponseItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of a data source as a db.
 */
class LocalDataSource internal constructor(
    private val db: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
)  {

    fun observeActivities(): LiveData<List<ActivityEntity>> {

        return db.driverActDao().getAll()
    }

    fun observeTasks(): LiveData<List<TaskEntity>> {
        return  db.driverTaskDao().observeTasks()
    }

    suspend fun getTasks(): ResultWithStatus<List<TaskEntity>> = withContext(ioDispatcher) {
        return@withContext try {
            ResultWithStatus.Success(db.driverTaskDao().getTasks())
        } catch (e: Exception) {
            ResultWithStatus.Error(e)
        }
    }

    suspend fun saveTask(taskEntity: TaskEntity) {
        db.driverTaskDao().deleteTasks()
    }



    suspend fun deleteTasks() {
        db.driverTaskDao().deleteTasks()
    }

    suspend fun insertFromCommItem(data: CommResponseItem) {
        db.driverTaskDao().insertFromCommItem(data)
        db.driverActDao().insertFromCommItem(data)
    }
}
