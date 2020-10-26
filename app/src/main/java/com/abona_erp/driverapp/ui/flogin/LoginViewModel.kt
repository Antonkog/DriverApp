package com.abona_erp.driverapp.ui.flogin

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.abona_erp.driverapp.data.Constant
import com.abona_erp.driverapp.data.local.preferences.PrivatePreferences
import com.abona_erp.driverapp.data.local.preferences.putAny
import com.abona_erp.driverapp.data.local.preferences.putLong
import com.abona_erp.driverapp.data.model.CommItem
import com.abona_erp.driverapp.data.remote.AppRepository
import com.abona_erp.driverapp.ui.base.BaseViewModel
import com.abona_erp.driverapp.ui.utils.UtilModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class LoginViewModel
@ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val app: AppRepository,
    private val prefs: SharedPreferences,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {


    val TAG = "LoginViewModel"

    enum class AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,        // The user has authenticated successfully
        INVALID_AUTHENTICATION  // Authentication failed
    }

    val authenticationState = MutableLiveData<AuthenticationState>()

    init {
        // In this example, the user is always unauthenticated when LoginViewModel is launched
        authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }

    fun refuseAuthentication() {
        authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }

    fun authenticate(username: String, password: String, clientId: Int) {
        prefs.putAny(Constant.mandantId, clientId)
        app.getAuthToken(Constant.grantTypeToken, username, password).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    Log.e(TAG, result.body().toString())
                    if (result.isSuccessful) {
                        Log.e(TAG, "got auth")
                        PrivatePreferences.setAccessToken(context, result.body()?.accessToken)

                        prefs.putLong(Constant.token_created, System.currentTimeMillis())
                        setFcmToken()
                        setDeviceProfile(UtilModel.getCommDeviceProfileItem(context))
                    } else {
                        Log.e(TAG, "INVALID_AUTHENTICATION $result")
                        authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
                    }
                },
                { error ->
                    Log.e(TAG, error?.localizedMessage ?: "INVALID_AUTHENTICATION")
                    authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
                }

            )
    }

    fun getEndpointActive(clientId: String) {
        app.getClientEndpoint(clientId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    Log.e(TAG, result.toString())
                    Log.e(TAG, "got endpoint")
                },
                { error ->
                    Log.e(TAG, error?.localizedMessage ?: "get endpoint error")
                }

            )
    }

    fun setFcmToken() {
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
        app.registerDevice(commItem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    if (result.isSuccess) {
                        Log.e(TAG, result.toString())
                        Log.e(TAG, "device set success")
                        authenticationState.value = AuthenticationState.AUTHENTICATED

                   //     FirebaseAnalytics.getInstance(context).logEvent("LogIn", null)

                    } else {
                        Log.e(TAG, "device set error: ${result.text}")
                        authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
                    }

                },
                { error ->
                    Log.e(TAG, error.localizedMessage ?: "error while setting deviceProfile")
                }
            )
    }


    fun userCancelledRegistration(): Boolean {
        // Clear existing registration data
//        registrationState.value = RegistrationState.COLLECT_PROFILE_DATA
//        authToken = ""
        return true
    }

}