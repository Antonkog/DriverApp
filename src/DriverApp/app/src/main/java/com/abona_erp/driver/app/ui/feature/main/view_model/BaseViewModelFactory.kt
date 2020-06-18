@file:Suppress("unchecked_cast")

package com.abona_erp.driver.app.ui.feature.main.view_model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abona_erp.driver.app.data.DriverDatabase
import com.abona_erp.driver.app.data.repository.DriverRepository
import com.abona_erp.driver.app.di.scopes.ActivityScope
import com.abona_erp.driver.app.manager.ApiManager
import com.abona_erp.driver.app.ui.feature.main.MainViewModel
import javax.inject.Inject

@ActivityScope
class BaseViewModelFactory @Inject constructor(
        private val app: Application,
        private val database: DriverDatabase,
        private val driverRepo: DriverRepository,
        private val apiManager: ApiManager) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(MainViewModel::class.java) ->
            MainViewModel(app) as T //in future inject only classes that we will use, don't create them using app context, as it may produce memory leaks and other errors.

        modelClass.isAssignableFrom(CMRViewModel::class.java) ->
            PendingViewModel(app) as T

        modelClass.isAssignableFrom(CMRViewModel::class.java) ->
            RunningViewModel(app) as T
        modelClass.isAssignableFrom(CMRViewModel::class.java) ->
            CMRViewModel(app) as T

        modelClass.isAssignableFrom(CMRViewModel::class.java) ->
            CompletedViewModel(app) as T

        else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.canonicalName}")
    }
}