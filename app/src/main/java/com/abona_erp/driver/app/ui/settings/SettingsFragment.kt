package com.abona_erp.driver.app.ui.settings

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
import com.abona_erp.driver.app.R
import com.abona_erp.driver.app.data.Constant
import com.abona_erp.driver.app.databinding.DriverActFragmentBinding
import com.abona_erp.driver.app.databinding.SettingsFragmentBinding
import com.abona_erp.driver.app.ui.activities.ActivityAdapter
import com.abona_erp.driver.app.ui.base.BaseFragment
import com.abona_erp.driver.app.ui.utils.DeviceUtils
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