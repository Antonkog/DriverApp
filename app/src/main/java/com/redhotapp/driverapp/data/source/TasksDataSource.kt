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
 * Main entry point for accessing tasks data.
 */
interface TasksDataSource {

    fun observeTasks(): LiveData<Result<List<Task>>>

    suspend fun getTasks(): Result<List<Task>>
    suspend fun deleteTask(taskId: Int)
    suspend fun refreshTask(taskId: String)
    suspend fun getTask(taskId: Int): Result<Task>
    suspend fun saveTask(task: Task)
    suspend fun completeTask(task: Task)
    suspend fun completeTask(taskId: Int)
    suspend fun activateTask(task: Task)
    suspend fun activateTask(taskId: Int)
    fun observeTask(taskId: Int): LiveData<Result<Task>>
}
