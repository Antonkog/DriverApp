package com.redhotapp.driverapp.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.databinding.HomeFragmentBinding
import com.redhotapp.driverapp.databinding.LoginFragmentBinding
import com.redhotapp.driverapp.ui.base.BaseFragment
import com.redhotapp.driverapp.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    val TAG : String  ="LoginFragment"

    private val loginViewModel by viewModels<LoginViewModel> ()
//    val loginViewModel: LoginViewModel by navGraphViewModels(R.id.nav_login) for scoped in graph check if need

    private lateinit var loginBinding: LoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.login_fragment, container, false)

        loginBinding = LoginFragmentBinding.bind(view).apply {
            viewmodel = loginViewModel
        }

        loginBinding.lifecycleOwner = this.viewLifecycleOwner

        loginViewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> showWelcomeMessage()
                LoginViewModel.AuthenticationState.UNAUTHENTICATED -> Toast.makeText(requireContext(), R.string.need_log_in, Toast.LENGTH_LONG ).show()
            }
        })


        loginBinding.buttonTest.setOnClickListener {
            loginBinding.editClientId.setText(R.string.client_id_test)
            loginBinding.editName.setText(R.string.name_test)
            loginBinding.editPassword.setText(R.string.password_test)
        }

        loginBinding.buttonLogIn.setOnClickListener {
            loginViewModel.authenticate(
                        loginBinding.editName.text.toString(),
                        loginBinding.editPassword.text.toString(),
                                loginBinding.editClientId.text.toString()

            )
        }
        return view
    }

    private fun showWelcomeMessage() {
        Log.e(TAG, "navigating home")
        findNavController().navigate(R.id.nav_home)
    }
}