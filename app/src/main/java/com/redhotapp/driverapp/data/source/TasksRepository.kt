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

package com.redhotapp.driverapp.data.source

import androidx.lifecycle.LiveData
import com.redhotapp.driverapp.data.Result
import com.redhotapp.driverapp.data.source.local.Task

/**
 * Interface to the data layer.
 */
interface TasksRepository {

    fun observeTasks(): LiveData<Result<List<Task>>>

    fun getTasks(forceUpdate: Boolean = false): Result<List<Task>>

    fun observeTask(taskId: Int): LiveData<Result<Task>>

    fun refreshTasks()

    fun refreshTask(taskId: Int)

    fun completeTask(taskId: Int)

    fun activateTask(taskId: Int)

    fun clearCompletedTasks()

    fun deleteAllTasks()

    fun deleteTask(taskId: Int)
    fun getTask(taskId: String): Result<Task>
    fun getTasks(): Result<List<Task>>
    fun saveTask(task: Task)
}
