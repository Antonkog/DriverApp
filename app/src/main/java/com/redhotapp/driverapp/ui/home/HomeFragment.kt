package com.redhotapp.driverapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.databinding.HomeFragmentBinding
import com.redhotapp.driverapp.databinding.LoginFragmentBinding
import com.redhotapp.driverapp.ui.base.BaseFragment
import com.redhotapp.driverapp.ui.login.LoginViewModel
import com.redhotapp.driverapp.ui.utils.DeviceUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    val TAG = "HomeFragment"
    private val homeViewModel by viewModels<HomeViewModel> ()
//    val homeViewModel: HomeViewModel by navGraphViewModels(R.id.nav_home)

    private lateinit var homeBinding: HomeFragmentBinding


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)

                homeBinding = HomeFragmentBinding.inflate(layoutInflater, container, false).apply {
                    viewmodel = homeViewModel

                }
        homeBinding = HomeFragmentBinding.bind(view).apply {
            viewmodel = homeViewModel
        }


        homeBinding.lifecycleOwner = this.viewLifecycleOwner

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            homeBinding.textHome.text = it
            Log.e(TAG, "has text")
        })
        homeBinding.button.setOnClickListener { view ->  homeViewModel.getAllTasks(DeviceUtils.getUniqueID(context)) }

        if(!homeViewModel.loggedIn()) findNavController().navigate(R.id.nav_login)
        return view
    }
}
