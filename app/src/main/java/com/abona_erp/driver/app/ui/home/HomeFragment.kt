package com.abona_erp.driver.app.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.abona_erp.driver.app.R
import com.abona_erp.driver.app.databinding.HomeFragmentBinding
import com.abona_erp.driver.app.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    val TAG = "HomeFragment"
    private val homeViewModel by viewModels<HomeViewModel> ()
//    val homeViewModel: HomeViewModel by navGraphViewModels(R.id.nav_home)
    private var adapter = TasksAdapter(null)

    private lateinit var homeBinding: HomeFragmentBinding


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)

//        homeBinding = HomeFragmentBinding.inflate(layoutInflater, container, false).apply {
//            viewmodel = homeViewModel
//        }
        homeBinding = HomeFragmentBinding.bind(view).apply {
            viewmodel = homeViewModel
        }


        homeBinding.lifecycleOwner = this.viewLifecycleOwner


        homeBinding.tasksPager.adapter = adapter

        TabLayoutMediator(homeBinding.tabLayout, homeBinding.tasksPager) { _, _ ->
            //Some implementation
//            tab.text = "OBJECT ${(position + 1)}"


        }.attach()

        homeBinding.tasksPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                homeViewModel.setVisibleTaskID(adapter.data[position])
            }
        })

        homeViewModel.mutableTasks.observe(viewLifecycleOwner, Observer {
            if(it!= null && it.isNotEmpty())
            adapter.swapData(it)
            else Log.e(TAG, "got empty or null tasks $it")
//            Log.e(TAG, "got tasks ${it}")
        })

        homeViewModel.error.observe(viewLifecycleOwner, Observer {
            if(it!= null && it.isNotEmpty())  homeBinding.textHome.text = it.toString()
        })

        if(!homeViewModel.loggedIn()) findNavController().navigate(R.id.nav_login)
        else homeViewModel.refreshTasksAsync()
        return view
    }
}
