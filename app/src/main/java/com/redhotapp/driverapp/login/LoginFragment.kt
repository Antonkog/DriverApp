package com.redhotapp.driverapp.login

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.ViewModelFactory
import com.redhotapp.driverapp.databinding.FragmentTasksBinding
import com.redhotapp.driverapp.tasks.TasksViewModel
import com.redhotapp.driverapp.util.getViewModelFactory


class LoginFragment : Fragment() {

    private val loginViewModel by viewModels<LoginViewModel> { getViewModelFactory() }
    private lateinit var loginDataBinding: FragmentTasksBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
//
//        val usernameEditText = view.findViewById<EditText>(R.id.username)
//        val passwordEditText = view.findViewById<EditText>(R.id.password)
//        val loginButton = view.findViewById<Button>(R.id.login)
//        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.loading)
//        loginDataBinding = TasksFragBinding.inflate(inflater, container, false).apply {
//            viewmodel = viewModel
//        }
//        loginViewModel.loginFormState.observe(this,
//            Observer { loginFormState ->
//                if (loginFormState == null) {
//                    return@Observer
//                }
//                loginButton.isEnabled = loginFormState.isDataValid
//                loginFormState.usernameError?.let {
//                    usernameEditText.error = getString(it)
//                }
//                loginFormState.passwordError?.let {
//                    passwordEditText.error = getString(it)
//                }
//            })
//
//        loginViewModel.loginResult.observe(this,
//            Observer { loginResult ->
//                loginResult ?: return@Observer
//                loadingProgressBar.visibility = View.GONE
//                loginResult.error?.let {
//                    showLoginFailed(it)
//                }
//                loginResult.success?.let {
//                    updateUiWithUser(it)
//                }
//            })
//
//        val afterTextChangedListener = object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//                // ignore
//            }
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                // ignore
//            }
//
//            override fun afterTextChanged(s: Editable) {
//                loginViewModel.loginDataChanged(
//                    usernameEditText.text.toString(),
//                    passwordEditText.text.toString()
//                )
//            }
//        }
//        usernameEditText.addTextChangedListener(afterTextChangedListener)
//        passwordEditText.addTextChangedListener(afterTextChangedListener)
//        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                loginViewModel.login(
//                    usernameEditText.text.toString(),
//                    passwordEditText.text.toString()
//                )
//            }
//            false
//        }
//
//        loginButton.setOnClickListener {
//            loadingProgressBar.visibility = View.VISIBLE
//            loginViewModel.login(
//                usernameEditText.text.toString(),
//                passwordEditText.text.toString()
//            )
//        }
//    }
//
//    private fun updateUiWithUser(model: LoggedInUserView) {
//        val welcome = getString(R.string.welcome) + model.displayName
//        // TODO : initiate successful logged in experience
//        val appContext = context?.applicationContext ?: return
//        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
//    }
//
//    private fun showLoginFailed(@StringRes errorString: Int) {
//        val appContext = context?.applicationContext ?: return
//        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
//    }
}