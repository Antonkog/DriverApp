package com.redhotapp.driverapp.ui.login

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.redhotapp.driverapp.data.Constant
import com.redhotapp.driverapp.data.local.preferences.Preferences
import com.redhotapp.driverapp.data.remote.ApiRepository
import com.redhotapp.driverapp.ui.base.BaseViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

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
                { error -> Log.e(TAG, error.localizedMessage)
                    authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
                }

            )
    }



    fun userCancelledRegistration() : Boolean {
        // Clear existing registration data
//        registrationState.value = RegistrationState.COLLECT_PROFILE_DATA
//        authToken = ""
        return true
    }

}