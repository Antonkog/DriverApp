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
import com.redhotapp.driverapp.data.remote.ApiRepository
import com.redhotapp.driverapp.ui.base.BaseViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

class LoginViewModel
@ViewModelInject constructor(private val api: ApiRepository, private val gson: Gson, @Assisted private val savedStateHandle: SavedStateHandle) : BaseViewModel() {


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
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                Log.e(LoginViewModel::class.java.canonicalName, it.toString())
            }

//        if (!true) {
//            authenticationState.value = AuthenticationState.AUTHENTICATED
//        } else {
//            authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
//        }
    }



    fun userCancelledRegistration() : Boolean {
        // Clear existing registration data
//        registrationState.value = RegistrationState.COLLECT_PROFILE_DATA
//        authToken = ""
        return true
    }

}