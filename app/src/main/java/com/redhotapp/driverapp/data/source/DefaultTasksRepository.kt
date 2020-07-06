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
import com.redhotapp.driverapp.Constants
import com.redhotapp.driverapp.data.Result
import com.redhotapp.driverapp.data.source.local.Task
import com.redhotapp.driverapp.data.source.net.ApiService
import com.redhotapp.driverapp.util.preferences.Preferences
import com.redhotapp.driverapp.util.wrapEspressoIdlingResource
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Default implementation of [TasksRepository]. Single entry point for managing tasks' data.
 */
class DefaultTasksRepository @Inject constructor(
    private val apiService: ApiService,
    private val tasksLocalDataSource: TasksDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksRepository {

    override fun observeTask(taskId: Int): LiveData<Result<Task>> {
        TODO("Not yet implemented")
    }

    override fun refreshTasks() {
        TODO("Not yet implemented")
    }

    override fun refreshTask(taskId: Int) {
        TODO("Not yet implemented")
    }

    override fun completeTask(taskId: Int) {
        TODO("Not yet implemented")
    }

    override fun activateTask(taskId: Int) {
        TODO("Not yet implemented")
    }

    override fun clearCompletedTasks() {
        TODO("Not yet implemented")
    }

    override fun deleteAllTasks() {
        TODO("Not yet implemented")
    }

    override fun deleteTask(taskId: Int) {
        TODO("Not yet implemented")
    }

    override fun getTask(taskId: Int): Result<Task> {
        TODO("Not yet implemented")
    }

    override fun saveTask(task: Task) {
        TODO("Not yet implemented")
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return tasksLocalDataSource.observeTasks()
    }

    override fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        TODO("Not yet implemented")
    }

    override fun getTasks(): Result<List<Task>> {
        TODO("Not yet implemented")
    }


    override fun getTask(deviceId: String): Result<List<Task>> {
        wrapEspressoIdlingResource {

             apiService.getAllTasks(deviceId)
                 .observeOn(mainThread()).subscribeOn(Schedulers.computation())
        }
    }

//    override suspend fun saveTask(task: Task) {
//        coroutineScope {
//            launch { tasksRemoteDataSource.saveTask(task) }
//            launch { tasksLocalDataSource.saveTask(task) }
//        }
//    }

}
