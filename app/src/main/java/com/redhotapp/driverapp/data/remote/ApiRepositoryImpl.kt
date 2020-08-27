package com.redhotapp.driverapp.data.remote

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.redhotapp.driverapp.App
import com.redhotapp.driverapp.data.local.db.ActivityEntity
import com.redhotapp.driverapp.data.local.db.AppDatabase
import com.redhotapp.driverapp.data.local.db.TaskEntity
import com.redhotapp.driverapp.data.model.*
import com.redhotapp.driverapp.data.remote.rabbitMQ.RabbitService
import com.redhotapp.driverapp.ui.utils.DeviceUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class ApiRepositoryImpl(val appDataBase: AppDatabase, val rabbit : RabbitService, val api : ApiService, val authService: AuthService) : ApiRepository {
    val TAG = "ApiRepositoryImpl"
    override fun getLatestOrder(id: String): Observable<LatestOrder> {
       return  rabbit.getLastOrder(id)
    }

   override suspend fun refreshData(deviceId: String) {
         withContext(Dispatchers.IO) {
            api.getAllTasks(deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> Log.e(TAG, result.toString())
                        appDataBase.driverTaskDao().insertFromCommItem(result)
                    },
                    { e ->
                        Log.e(TAG, "error while get tasks " +  e.localizedMessage)
                    })
        }
    }

    override fun getAllTasks(deviceId: String): LiveData<List<TaskEntity>> {
        return appDataBase.driverTaskDao().getAll()
    }

    override fun getAllActivity(deviceId: String): LiveData<List<ActivityEntity>> {
        return appDataBase.driverActDao().getAll()
    }

    override fun registerDevice(commItem: CommItem): Observable<ResultOfAction> {
        return api.deviceProfile(commItem)
    }
//    override fun getDocuments(
//        mandantId: Int,
//        orderNo: Int,
//        deviceId: String
//    ): Single<List<AppFileInterchangeItem>> {
//        return  api.getDocuments(mandantId, orderNo, deviceId)
//    }
//    override fun getAllTasks(deviceId: String): Observable<CommResponseItem> {
//        return api.getAllTasks(deviceId)
//    }

    override fun postActivity(deviceId: String): Observable<CommResponseItem> {
        return api.postActivity(deviceId)
    }

    override fun getClientEndpoint(clientId: String): Observable<JsonObject> {
        return  authService.getClientEndpoint(clientId)
    }

    override fun getAuthToken(grantType : String, userName : String, password : String): Observable<Response<TokenResponse>> {
        return  api.authentication(grantType, userName, password)
    }
}