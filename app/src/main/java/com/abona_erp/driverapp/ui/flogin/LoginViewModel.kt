package com.abona_erp.driverapp.ui.flogin

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.abona_erp.driverapp.data.Constant
import com.abona_erp.driverapp.data.local.preferences.PrivatePreferences
import com.abona_erp.driverapp.data.local.preferences.putAny
import com.abona_erp.driverapp.data.local.preferences.putLong
import com.abona_erp.driverapp.data.model.CommItem
import com.abona_erp.driverapp.data.model.ResultOfAction
import com.abona_erp.driverapp.data.model.ServerUrlResponse
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.data.remote.ResultWrapper
import com.abona_erp.driverapp.data.remote.data
import com.abona_erp.driverapp.data.remote.succeeded
import com.abona_erp.driverapp.ui.base.BaseViewModel
import com.abona_erp.driverapp.ui.utils.UtilModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.system.measureTimeMillis

class LoginViewModel
@ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AppRepository,
    private val prefs: SharedPreferences,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val TAG = "LoginViewMode l"


    enum class AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,        // The user has authenticated successfully
        INVALID_AUTHENTICATION  // Authentication failed
    }

    val authenticationState = MutableLiveData<AuthenticationState>()
    val error = MutableLiveData<String>()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        error.postValue(exception.message)
        Log.e(TAG, exception.message)
    }

    init {
        // In this example, the user is always unauthenticated when LoginViewModel is launched
        authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }

    fun refuseAuthentication() {
        authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }

    fun authenticate(username: String, password: String, clientId: Int) {
        prefs.putAny(Constant.mandantId, clientId)
        //at first set endpoing, then set auth token, then set device profile.
        viewModelScope.launch(exceptionHandler) {

            val time = measureTimeMillis {

                setFcmToken() //not using return val as saved in private preferences

                val endPointResponse: ResultWrapper<ServerUrlResponse> = getClientEndpoint(clientId)

                if (endPointResponse.succeeded) {
                    endPointResponse.data?.WebService?.let {
                        setNewClientEndpoint(it)
                    }
                } else {
                    authenticationState.postValue(AuthenticationState.INVALID_AUTHENTICATION)
                    error.postValue(endPointResponse.toString())
                }
                //if we dont get new url, we using standard common url, for now, so i put next`code outside of bracers.


                val tokenResult = repository.getAuthToken(Constant.grantTypeToken, username, password)

                if (tokenResult.succeeded) {
                    PrivatePreferences.setAccessToken(
                        context,
                        tokenResult.data?.accessToken
                    )
                    prefs.putLong(Constant.token_created, System.currentTimeMillis())
                } else{
                    authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
                    error.postValue(endPointResponse.toString())
                }


                val deviceProfileResponse = setDeviceProfile(UtilModel.getCommDeviceProfileItem(context))

                if (deviceProfileResponse.succeeded) {
                    Log.d(TAG, "device set success $deviceProfileResponse")
                    authenticationState.value = AuthenticationState.AUTHENTICATED
                    //     FirebaseAnalytics.getInstance(context).logEvent("LogIn", null)

                } else {
                    authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
                    error.postValue(endPointResponse.toString())
                }

            }//time debug
            Log.d(TAG, "Device authorization completed in $time ms")
        }//viewmodel scope
    }

    /**
     * this method for getting new Endpoint for each client, and change base url to new one
     * see:   Manifest:      android:usesCleartextTraffic="true" because auth url is not https
     */
    private suspend fun getClientEndpoint(clientId: Int): ResultWrapper<ServerUrlResponse> {
       return repository.getClientEndpoint(""+clientId)
    }

    private fun setNewClientEndpoint(baseUrl: String) {
        PrivatePreferences.setEndpoint(context, baseUrl)
    }

    private suspend fun setFcmToken() = suspendCancellableCoroutine<String> { continuation ->
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    continuation.resumeWithException(Throwable("getInstanceId failed" + task.exception))
                    return@OnCompleteListener
                }
                // Get new Instance ID token

                task.result?.token?.let {
                    // Log and toast
                    PrivatePreferences.setFCMToken(context, it)
                    continuation.resume(it)
                }
            })
    }

    private suspend fun setDeviceProfile(commItem: CommItem): ResultWrapper<ResultOfAction>  {
        return repository.registerDevice(commItem)
    }


    fun userCancelledRegistration(): Boolean {
        // Clear existing registration data
//        registrationState.value = RegistrationState.COLLECT_PROFILE_DATA
//        authToken = ""
        return true
    }

}