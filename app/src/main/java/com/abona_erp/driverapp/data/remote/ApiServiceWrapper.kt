package com.abona_erp.driverapp.data.remote

import com.abona_erp.driverapp.MainViewModel
import com.abona_erp.driverapp.data.local.LocalDataSource
import com.abona_erp.driverapp.data.model.*
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.events.RxBusEvent
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Created by Anton Kogan email: Akogan777@gmail.com on 11/9/2020
 */
class ApiServiceWrapper(val api: ApiService, val localDataSource: LocalDataSource) {

     suspend  fun authentication(
        grantType: String,
        userName: String,
        password: String
    ): ResultWrapper<TokenResponse>{
         RxBus.publish(RxBusEvent.RequestStatus(MainViewModel.Status(null, MainViewModel.StatusType.LOADING)))
         return try{
             val result =   ResultWrapper.Success(api.authentication(grantType, userName, password))
             RxBus.publish(RxBusEvent.RequestStatus(MainViewModel.Status(result.toString(), MainViewModel.StatusType.COMPLETE)))
             result
         }catch (ex: Exception){
             RxBus.publish(RxBusEvent.RequestStatus(MainViewModel.Status(ex.message, MainViewModel.StatusType.ERROR)))
             ResultWrapper.Error(ex)
         }
     }

    suspend fun updateTasksFromServer(deviceId: String?){
        RxBus.publish(RxBusEvent.RequestStatus(MainViewModel.Status(null, MainViewModel.StatusType.LOADING)))

        val remoteTasks = api.getAllTasks(deviceId)
        if (remoteTasks.isSuccess && !remoteTasks.isException) {
            localDataSource.updateFromCommItem(remoteTasks)
            RxBus.publish(RxBusEvent.RequestStatus(MainViewModel.Status(null, MainViewModel.StatusType.COMPLETE)))
        } else {
            RxBus.publish(RxBusEvent.RequestStatus(MainViewModel.Status("updateTasks exception:  ${remoteTasks.text}", MainViewModel.StatusType.ERROR)))
            throw java.lang.Exception("updateTasks exception:  ${remoteTasks.text} ")
        }
    }

    suspend fun updateDocumentsFromServer(
        mandantId: Int,
        orderNo: Int,
        deviceId: String
    ){
         try {
             RxBus.publish(RxBusEvent.RequestStatus(MainViewModel.Status(null, MainViewModel.StatusType.LOADING)))

             val resp = api.getDocuments(mandantId, orderNo, deviceId)

             RxBus.publish(RxBusEvent.RequestStatus(MainViewModel.Status(null, MainViewModel.StatusType.COMPLETE)))

             resp?.let {
                 if (it.isNotEmpty()) {
                    localDataSource.deleteDocuments()
                    localDataSource.insertDocumentResponse(it)
                }
            }

        } catch (ex: java.lang.Exception) {
            RxBus.publish(RxBusEvent.RequestStatus(MainViewModel.Status(ex.message, MainViewModel.StatusType.ERROR)))
             throw java.lang.Exception("update Documents exception:  ${ex.message} ")
        }
    }


    suspend fun setDeviceProfile(commItem: CommItem?): ResultWrapper<ResultOfAction>{
        RxBus.publish(RxBusEvent.RequestStatus(MainViewModel.Status(null, MainViewModel.StatusType.LOADING)))
        return try {
            val result =
                ResultWrapper.Success(api.setDeviceProfile(commItem))
            RxBus.publish(RxBusEvent.RequestStatus(MainViewModel.Status(result.toString(), MainViewModel.StatusType.COMPLETE)))
            result
        } catch (ex: Exception){
            RxBus.publish(RxBusEvent.RequestStatus(MainViewModel.Status(ex.message, MainViewModel.StatusType.ERROR)))
            return ResultWrapper.Error(ex)
        }
    }

    suspend fun postActivityChange(commItem: CommItem): ResultWrapper<ResultOfAction> {
        RxBus.publish(RxBusEvent.RequestStatus(MainViewModel.Status(null, MainViewModel.StatusType.LOADING)))
        return try {
            val result = ResultWrapper.Success(api.postActivityChange(commItem))
            RxBus.publish(RxBusEvent.RequestStatus(MainViewModel.Status(result.toString(), MainViewModel.StatusType.COMPLETE)))
            result
        } catch (ex: Exception){
            RxBus.publish(RxBusEvent.RequestStatus(MainViewModel.Status(ex.message, MainViewModel.StatusType.ERROR)))
            ResultWrapper.Error(ex)
        }
    }



    suspend fun confirmTask(commItem: CommItem): ResultWrapper<ResultOfAction>{
        return try{
            ResultWrapper.Success(api.confirmTask(commItem))
        } catch (ex: java.lang.Exception){
            ResultWrapper.Error(ex)
        }
    }


    fun uploadDocument(
        mandantId: RequestBody?,
       orderNo: RequestBody?,
        taskId: RequestBody?,
         driverNo: RequestBody?,
         documentType: RequestBody?,
         file: MultipartBody.Part?
    ):  Single<UploadResult>{
        return api.uploadDocument(mandantId, orderNo, taskId, driverNo, documentType, file)
    }
}