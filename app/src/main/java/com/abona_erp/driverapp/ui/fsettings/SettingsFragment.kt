package com.abona_erp.driverapp.ui.fsettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.databinding.SettingsFragmentBinding
import com.abona_erp.driverapp.ui.base.BaseFragment
import com.abona_erp.driverapp.ui.utils.DeviceUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment() {
    val TAG = "DriverActFragment"

    private val settingsViewModel by viewModels<SettingsViewModel>()

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

        settingFragmentBinding.textSettings.text =  getString(R.string.deviceId) + DeviceUtils.getUniqueID(context)
        return settingFragmentBinding.root
    }
}