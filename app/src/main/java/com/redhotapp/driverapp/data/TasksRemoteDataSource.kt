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
package com.redhotapp.driverapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.redhotapp.driverapp.data.Result.Error
import com.redhotapp.driverapp.data.Result.Success
import com.redhotapp.driverapp.data.source.TasksDataSource
import com.redhotapp.driverapp.data.source.TasksRepository
import com.redhotapp.driverapp.data.source.local.Task
import java.util.LinkedHashMap

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
class TasksRemoteDataSource : TasksRepository {

    private var TASKS_SERVICE_DATA: LinkedHashMap<Int, Task> = LinkedHashMap()

    private val observableTasks = MutableLiveData<Result<List<Task>>>()

    override fun refreshTasks() {
        observableTasks.postValue(getTasks())
    }

    override fun refreshTask(taskId: Int) {
        refreshTasks()
    }

    override fun completeTask(taskId: Int) {
        TODO("Not yet implemented")
    }

    override fun activateTask(taskId: Int) {
        TODO("Not yet implemented")
    }


    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return observableTasks
    }

    override fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        TODO("Not yet implemented")
    }


    override fun observeTask(taskId: Int): LiveData<Result<Task>> {
        return observableTasks.map { tasks ->
            when (tasks) {
                is Result.Loading -> Result.Loading
                is Error -> Error(tasks.exception)
                is Success -> {
                    val task = tasks.data.firstOrNull() { it.taskId == taskId }
                        ?: return@map Error(Exception("Not found"))
                    Success(task)
                }
            }
        }
    }

    override fun getTask(taskId: String): Result<Task> {
        TASKS_SERVICE_DATA[taskId]?.let {
            return Success(it)
        }
        return Error(Exception("Could not find task"))
    }

    override  fun getTasks(): Result<List<Task>> {
        return Success(TASKS_SERVICE_DATA.values.toList())
    }

    override  fun saveTask(task: Task) {
        TASKS_SERVICE_DATA[task.taskId] = task
    }



    override  fun clearCompletedTasks() {
        TASKS_SERVICE_DATA = TASKS_SERVICE_DATA.filterValues {
             it.status?.contains("123")?:false
        } as LinkedHashMap<Int, Task>
    }

    override fun deleteTask(taskId: Int) {
        TASKS_SERVICE_DATA.remove(taskId)
        refreshTasks()
    }

    override  fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
        refreshTasks()
    }
}
