package com.redhotapp.driverapp.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.databinding.DriverActFragmentBinding
import com.redhotapp.driverapp.ui.base.BaseFragment

class DriverActFragment : BaseFragment() {


    private val loginViewModel by viewModels<DriverActViewModel> ()


    private lateinit var viewDataBinding: DriverActFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.driver_act_fragment, container, false)
    }

}