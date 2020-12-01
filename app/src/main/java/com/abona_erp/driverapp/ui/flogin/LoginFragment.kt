package com.abona_erp.driverapp.ui.flogin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.Constant
import com.abona_erp.driverapp.databinding.LoginFragmentBinding
import com.abona_erp.driverapp.ui.base.BaseFragment
import com.abona_erp.driverapp.ui.utils.DeviceUtils
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    val TAG: String = "LoginFragment"

    private val loginViewModel by viewModels<LoginViewModel>()
//    val loginViewModel: LoginViewModel by navGraphViewModels(R.id.nav_login) for scoped in graph check if need

    private lateinit var loginBinding: LoginFragmentBinding

    val args: LoginFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.login_fragment, container, false)

        loginBinding = LoginFragmentBinding.bind(view).apply {
            viewmodel = loginViewModel
        }

        loginBinding.lifecycleOwner = this.viewLifecycleOwner

        loginViewModel.authenticationState.observe(
            viewLifecycleOwner,
            { authenticationState ->
                when (authenticationState) {
                    LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                        navigateHome()
                        logFirebaseLogin()
                    }
                    LoginViewModel.AuthenticationState.INVALID_AUTHENTICATION -> Toast.makeText(
                        requireContext(),
                        R.string.error_log_in,
                        Toast.LENGTH_LONG
                    ).show()
                    LoginViewModel.AuthenticationState.UNAUTHENTICATED -> Toast.makeText(
                        requireContext(),
                        R.string.need_log_in,
                        Toast.LENGTH_LONG
                    ).show()
                    else -> Toast.makeText(
                        requireContext(),
                        R.string.error_log_in,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

        loginViewModel.error.observe(viewLifecycleOwner, { error ->
            loginBinding.textError?.text = error
        })

        loginBinding.buttonTest.setOnClickListener {
            loginBinding.editClientId.setText(Constant.testMandantId.toString())
            loginBinding.editName.setText(R.string.name_test)
            loginBinding.editPassword.setText(R.string.password_test)
        }

        loginBinding.buttonLogIn.setOnClickListener {
            if(loginBinding.editName.text.isNotBlank() &&
                loginBinding.editPassword.text.isNotBlank()&&
                loginBinding.editClientId.text.isNotBlank())
            loginViewModel.authenticate(
                loginBinding.editName.text.toString(),
                loginBinding.editPassword.text.toString(),
                Integer.parseInt(loginBinding.editClientId.text.toString())
            )
        }

        if(args.autoLogin){
            loginViewModel.resetAuthToken(resources.getString(R.string.password_test),resources.getString(R.string.name_test))
        }
        return view
    }

    private fun navigateHome() {
        findNavController().popBackStack()
    }

    private fun logFirebaseLogin() {
        val firebaseAnalytics = Firebase.analytics
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
            param("DeviceId", DeviceUtils.getUniqueID(context))
        }
    }
}