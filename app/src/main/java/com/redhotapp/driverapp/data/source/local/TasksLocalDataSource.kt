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
package com.redhotapp.driverapp.data.source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.redhotapp.driverapp.data.Result
import com.redhotapp.driverapp.data.Result.Error
import com.redhotapp.driverapp.data.Result.Success
import com.redhotapp.driverapp.data.source.TasksDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of a data source as a db.
 */
class TasksLocalDataSource internal constructor(
    private val tasksDao: TasksDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksDataSource {

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return tasksDao.observeTasks().map {
            Success(it)
        }
    }

    override fun observeTask(taskId: Int): LiveData<Result<Task>> {
        return tasksDao.observeTaskById(taskId).map {
            Success(it)
        }
    }

    override suspend fun refreshTask(taskId: String) {
        // NO-OP
    }

    override suspend fun getTasks(): Result<List<Task>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(tasksDao.getTasks())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun deleteTask(taskId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getTask(taskId: Int): Result<Task> = withContext(ioDispatcher) {
        try {
            val task = tasksDao.getTaskById(taskId)
            if (task != null) {
                return@withContext Success(task)
            } else {
                return@withContext Error(Exception("Task not found!"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override suspend fun saveTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.insertTask(task)
    }

    override suspend fun completeTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.updateCompleted(task.taskId, true)
    }

    override suspend fun completeTask(taskId: Int) {
        tasksDao.updateCompleted(taskId, true)
    }

    override suspend fun activateTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.updateCompleted(task.taskId, false)
    }


    override suspend fun activateTask(taskId: Int) {
        tasksDao.updateCompleted(taskId, false)
    }
}
