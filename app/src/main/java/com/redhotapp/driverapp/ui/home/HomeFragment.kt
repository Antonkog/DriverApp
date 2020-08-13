package com.redhotapp.driverapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.kivi.remote.presentation.base.recycler.LazyAdapter
import com.kivi.remote.presentation.base.recycler.addItemDivider
import com.kivi.remote.presentation.base.recycler.initWithLinLay
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.data.model.AllTask
import com.redhotapp.driverapp.data.model.abona.TaskItem
import com.redhotapp.driverapp.databinding.HomeFragmentBinding
import com.redhotapp.driverapp.ui.base.BaseFragment
import com.redhotapp.driverapp.ui.utils.DeviceUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment(), LazyAdapter.OnItemClickListener<AllTask> {

    val TAG = "HomeFragment"
    private val homeViewModel by viewModels<HomeViewModel> ()
//    val homeViewModel: HomeViewModel by navGraphViewModels(R.id.nav_home)
    private var adapter = TasksAdapter(this)

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

        homeBinding.tasksPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                homeViewModel.setVisibleTaskID(adapter.data[position])
            }
        })

        homeViewModel.mutableTasks.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty())
            adapter.swapData(it)
            Log.e(TAG, "got tasks")
        })

        homeViewModel.error.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty())  homeBinding.textHome.text = it.toString()
        })

        if(!homeViewModel.loggedIn()) findNavController().navigate(R.id.nav_login)
        else homeViewModel.populateTasks(DeviceUtils.getUniqueID(context))
        return view
    }

    override fun onLazyItemClick(data: AllTask) {
        Toast.makeText(context, " on task click : ${data.taskId}", Toast.LENGTH_SHORT).show()
        val action = HomeFragmentDirections.actionNavHomeToNavActivities(data.taskId)
        findNavController().navigate(action)
    }
}
