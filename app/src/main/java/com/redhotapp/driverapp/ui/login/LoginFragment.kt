package com.redhotapp.driverapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.databinding.LoginFragmentBinding

class LoginFragment : Fragment() {


    private val loginViewModel by viewModels<LoginViewModel> ()


    private lateinit var viewDataBinding: LoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.login_fragment, container, false)


        val navController = findNavController()
        loginViewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> showWelcomeMessage()
                LoginViewModel.AuthenticationState.UNAUTHENTICATED -> navController.navigate(R.id.nav_login)
            }
        })

        return root
    }

    private fun showWelcomeMessage() {
        viewDataBinding.welcomeTextView.text = "welcome"
    }
}