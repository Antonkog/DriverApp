package com.redhotapp.driverapp.ui.login

import android.content.Context
import android.os.AsyncTask
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.redhotapp.driverapp.data.Constant
import com.redhotapp.driverapp.data.local.preferences.Preferences
import com.redhotapp.driverapp.data.model.abona.CommItem
import com.redhotapp.driverapp.data.model.abona.DataType
import com.redhotapp.driverapp.data.model.abona.DeviceProfileItem
import com.redhotapp.driverapp.data.model.abona.Header
import com.redhotapp.driverapp.data.remote.ApiRepository
import com.redhotapp.driverapp.ui.base.BaseViewModel
import com.redhotapp.driverapp.ui.utils.DeviceUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class LoginViewModel
@ViewModelInject constructor(@ApplicationContext private val context: Context, private val api: ApiRepository, private val gson: Gson, @Assisted private val savedStateHandle: SavedStateHandle) : BaseViewModel() {


    val TAG = "LoginViewModel"
    enum class AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED  ,        // The user has authenticated successfully
        INVALID_AUTHENTICATION  // Authentication failed
    }

    val authenticationState = MutableLiveData<AuthenticationState>()
    init {
        // In this example, the user is always unauthenticated when MainActivity is launched
        authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }

    fun refuseAuthentication() {
        authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }

    fun authenticate(username: String, password: String, clientId: String) {
        api.getAuthToken(Constant.grantTypeToken, username, password).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.e(TAG, result.body().toString())
                    Log.e(TAG, "got auth")

                    authenticationState.value = AuthenticationState.AUTHENTICATED
                    Preferences.setAccessToken(context, result.body()?.accessToken)

                },
                { error ->
                    Log.e(TAG, error.localizedMessage)
                    authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
                }

            )

//        setFcmToken()

//        setDeviceProfile(getCommItem())

    }

    fun getEndpointActive(clientId: String) {
        api.getClientEndpoint("3")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.e(TAG, result.toString())
                    Log.e(TAG, "got endpoint")
                },
                { error ->
                    Log.e(TAG, error.localizedMessage)
                }

            )

    }

    fun setFcmToken(){
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                Toast.makeText(context, token, Toast.LENGTH_SHORT).show()

                Preferences.setFCMToken(context, token)
            })
    }


    fun setDeviceProfile(commItem: CommItem) {
        api.registerDevice(commItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.e(TAG, result.toString())
                    Log.e(TAG, "edvixce is set")
                },
                { error ->
                    Log.e(TAG, error.localizedMessage)
                }

            )

    }


    private fun getCommItem(): CommItem {
        val commItem = CommItem()
        val header = Header()
        header.dataType = DataType.DEVICE_PROFILE
        header.deviceId = DeviceUtils.getUniqueIMEI(context)
        commItem.header = header
        val deviceProfileItem = DeviceProfileItem()
        deviceProfileItem.instanceId = Preferences.getFCMToken(context)
        deviceProfileItem.deviceId = DeviceUtils.getUniqueIMEI(context)
        commItem.deviceProfileItem = deviceProfileItem
        return commItem
    }

    fun userCancelledRegistration() : Boolean {
        // Clear existing registration data
//        registrationState.value = RegistrationState.COLLECT_PROFILE_DATA
//        authToken = ""
        return true
    }

}