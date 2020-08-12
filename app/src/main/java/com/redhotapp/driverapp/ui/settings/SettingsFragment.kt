package com.redhotapp.driverapp.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kivi.remote.presentation.base.recycler.addItemDivider
import com.kivi.remote.presentation.base.recycler.initWithLinLay
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.data.Constant
import com.redhotapp.driverapp.databinding.DriverActFragmentBinding
import com.redhotapp.driverapp.databinding.SettingsFragmentBinding
import com.redhotapp.driverapp.ui.activities.ActivityAdapter
import com.redhotapp.driverapp.ui.base.BaseFragment
import com.redhotapp.driverapp.ui.utils.DeviceUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment() {
    val TAG = "DriverActFragment"

    private val settingsViewModel by viewModels<SettingsViewModel> ()

    private lateinit var settingFragmentBinding: SettingsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        settingFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.settings_fragment, container,
            false
        )

        settingFragmentBinding.viewmodel = settingsViewModel
        settingFragmentBinding.lifecycleOwner = this.viewLifecycleOwner


        return settingFragmentBinding.root
    }
}