package com.redhotapp.driverapp.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.redhotapp.driverapp.BuildConfig
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.data.Constant
import com.redhotapp.driverapp.data.local.preferences.PrivatePreferences
import com.redhotapp.driverapp.data.local.preferences.putAny
import com.redhotapp.driverapp.data.local.preferences.putLong
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
@ViewModelInject constructor(@ApplicationContext private val context: Context, private val api: ApiRepository, private val prefs: SharedPreferences, @Assisted private val savedStateHandle: SavedStateHandle) : BaseViewModel() {


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

    fun authenticate(username: String, password: String, clientId: Int) {
        prefs.putAny(context.resources.getString(R.string.mandantId), clientId)
        api.getAuthToken(Constant.grantTypeToken, username, password).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.e(TAG, result.body().toString())
                    Log.e(TAG, "got auth")
                    PrivatePreferences.setAccessToken(context, result.body()?.accessToken)
                    prefs.putLong(context.getString(R.string.token_created), System.currentTimeMillis())
                    setFcmToken()
                    setDeviceProfile(getCommItem())
                },
                { error ->
                    Log.e(TAG, error?.localizedMessage ?: "INVALID_AUTHENTICATION")
                    authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
                }

            )
    }

    fun getEndpointActive(clientId: String) {
        api.getClientEndpoint(clientId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.e(TAG, result.toString())
                    Log.e(TAG, "got endpoint")
                },
                { error ->
                    Log.e(TAG, error?.localizedMessage ?: "get endpoint error")
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

                PrivatePreferences.setFCMToken(context, token)
            })
    }


    fun setDeviceProfile(commItem: CommItem) {
        api.registerDevice(commItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.e(TAG, result.toString())
                    Log.e(TAG, "device set success")
                    authenticationState.value = AuthenticationState.AUTHENTICATED
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
        header.deviceId = DeviceUtils.getUniqueID(context)
        commItem.header = header

        val deviceProfileItem = DeviceProfileItem()
        deviceProfileItem.instanceId = PrivatePreferences.getFCMToken(context)
        deviceProfileItem.deviceId =  DeviceUtils.getUniqueID(context)
        deviceProfileItem.model = Build.MODEL
        deviceProfileItem.manufacturer = Build.MANUFACTURER
        val dfUtc: DateFormat = SimpleDateFormat(Constant.abonaDateFormat, Locale.getDefault())

        dfUtc.timeZone = TimeZone.getTimeZone(Constant.abonaTimeZone)
        val currentTimestamp = Date()
        deviceProfileItem.createdDate = dfUtc.format(currentTimestamp)
        deviceProfileItem.updatedDate = dfUtc.format(currentTimestamp)
        deviceProfileItem.languageCode = Locale.getDefault().toString().replace("_", "-")
        deviceProfileItem.versionCode = BuildConfig.VERSION_CODE
        deviceProfileItem.versionName = BuildConfig.VERSION_NAME
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