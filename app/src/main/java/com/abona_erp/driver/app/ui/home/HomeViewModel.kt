package com.abona_erp.driver.app.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.abona_erp.driver.app.App
import com.abona_erp.driver.app.data.Constant
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.abona_erp.driver.app.data.remote.ApiRepository
import com.abona_erp.driver.app.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.abona_erp.driver.app.data.local.preferences.PrivatePreferences
import com.abona_erp.driver.app.data.local.preferences.putAny
import com.abona_erp.driver.app.data.model.AllTask
import com.abona_erp.driver.app.ui.utils.DeviceUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class HomeViewModel @ViewModelInject constructor(@ApplicationContext private val context: Context, private val api: ApiRepository, private val  prefs: SharedPreferences, @Assisted private val savedStateHandle: SavedStateHandle) :  BaseViewModel() {  //taskRepo : ApiRepository,
    private val TAG = "HomeViewModel"

    val mutableTasks  = MutableLiveData<List<TaskEntity>> ()
    val error  = MutableLiveData<String> ()

    fun loggedIn(): Boolean {
        val currentTime = System.currentTimeMillis()
        val difference = currentTime - prefs.getLong(Constant. token_created,0)
        Log.i(TAG, "token time difference:  $difference")
        return ((difference < Constant.tokenUpdateHours * 3600 * 1000) // hours to seconds to mills
                && PrivatePreferences.getAccessToken(context) != null)
    }

    fun getTasks() {
       mutableTasks.postValue(api.getAllTasks(DeviceUtils.getUniqueID(context)).value)
    }

    fun refreshTasksAsync() = GlobalScope.async{
        api.refreshTasks(DeviceUtils.getUniqueID(context))
    }
//
//    fun userName(): Flowable<String> {
//        return dataSource.getUserById(USER_ID)
//            .map { user -> user.userName }
//    }
//
//    /**
//     * Update the user name.
//     * @param userName the new user name
//     * *
//     * @return a [Completable] that completes when the user name is updated
//     */
//    fun updateUserName(userName: String): Completable {
//        val user = User(USER_ID, userName)
//        return dataSource.insertUser(user)
//    }

    fun setVisibleTaskID(taskEntity: TaskEntity) {
        Log.e(TAG, "saving task " + taskEntity.taskId)
        prefs.putAny(Constant.currentVisibleTaskid, taskEntity.taskId)
    }

}