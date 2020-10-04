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
import com.abona_erp.driver.app.R
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.abona_erp.driver.app.databinding.HomeFragmentBinding
import com.abona_erp.driver.app.ui.base.BaseFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.kivi.remote.presentation.base.recycler.LazyAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment(), LazyAdapter.OnItemClickListener<TaskEntity> {

    val TAG = "HomeFragment"
    private val homeViewModel by viewModels<HomeViewModel>()

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

//        TabLayoutMediator(homeBinding.tabLayout, homeBinding.tasksPager) { _, _ ->
            //Some implementation
//            tab.text = "OBJECT ${(position + 1)}"
//        }.attach()



        homeBinding.tasksPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                homeViewModel.setVisibleTaskID(adapter.data[position])
            }
        })

        homeBinding.tabLayout.addOnTabSelectedListener(onTabSelectedListener())

        homeViewModel.tasks.observe(viewLifecycleOwner, Observer {
            homeViewModel.setTasks(it)
        })

        homeViewModel.filteredTasks.observe(viewLifecycleOwner, Observer {
            if (it != null && it.isNotEmpty()) {
                Log.e(TAG, "got tasks $it")
                adapter.swapData(it)
//                setAdapterPosition(it) //got new tasks, change position. NoSuchElementException: Collection contains no element matching the predicate.
            } else Log.e(TAG, "got empty or null tasks $it")
//            Log.e(TAG, "got tasks ${it}")
        })

        homeViewModel.error.observe(viewLifecycleOwner, Observer {
            if (it != null && it.isNotEmpty()) homeBinding.textHome.text = it.toString()
        })

        if (!homeViewModel.loggedIn()) findNavController().navigate(R.id.nav_login)
        // else homeViewModel.getTasks()
        return view
    }

    private fun onTabSelectedListener(): OnTabSelectedListener {
        return object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.tag) {
                    R.string.pending -> homeViewModel.filterPending()
                    R.string.running -> homeViewModel.filterRunning()
                    R.string.completed -> homeViewModel.filterCompleted()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        }
    }

    private fun setAdapterPosition(it: List<TaskEntity>) {
        if (homeViewModel.getVisibleTaskId() != 0) {
            val task =
                it.first { taskEntity -> taskEntity.taskId == homeViewModel.getVisibleTaskId() }
            homeBinding.tasksPager.setCurrentItem(it.indexOf(task),false) //setting position of current task, if it exist
        } else homeViewModel.setVisibleTaskID(it[0]) //else on create set id of first element.
    }

    override fun onLazyItemClick(data: TaskEntity) {
        Log.e(TAG, "got click taskid " + data.taskId)
    }
}
