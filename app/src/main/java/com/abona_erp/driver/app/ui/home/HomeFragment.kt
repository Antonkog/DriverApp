package com.abona_erp.driver.app.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abona_erp.driver.app.R
import com.abona_erp.driver.app.databinding.HomeFragmentBinding
import com.abona_erp.driver.app.ui.base.BaseFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.kivi.remote.presentation.base.recycler.LazyAdapter
import com.kivi.remote.presentation.base.recycler.addItemDivider
import com.kivi.remote.presentation.base.recycler.initWithLinLay
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment(), LazyAdapter.OnItemClickListener<TaskWithActivities> {

    val TAG = "HomeFragment"
    private val homeViewModel by viewModels<HomeViewModel>()

    //    val homeViewModel: HomeViewModel by navGraphViewModels(R.id.nav_home)
    private lateinit var adapter :TasksAdapter

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

        adapter =  TasksAdapter(this, findNavController())

        homeBinding.tasksRecycler.adapter = adapter

//        TabLayoutMediator(homeBinding.tabLayout, homeBinding.tasksPager) { _, _ ->
            //Some implementation
//            tab.text = "OBJECT ${(position + 1)}"
//        }.attach()


        homeBinding.tasksRecycler.addOnScrollListener( object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)


                try {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager //see initWithLinLay

                    val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                    val lastVisiblePosition  = layoutManager.findLastVisibleItemPosition()

                    val task = adapter.data[firstVisiblePosition]
                    homeViewModel.setVisibleTaskIDs(task)
                    Log.e(TAG, "visible task id = ${task.taskEntity.taskId} , first vis pos: $firstVisiblePosition , lastvispos =  $lastVisiblePosition"  )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

//                val id = adapter.getLastVisibleItemId()
//                Log.e(TAG,
//                    " adapter.getLastVisibleItemId() =  $id"
//                )
            }
        })

        homeBinding.tabLayout.addOnTabSelectedListener(onTabSelectedListener())

        homeViewModel.tasks.observe(viewLifecycleOwner, Observer {
            homeViewModel.setTasks(it)
        })

        homeViewModel.filteredTasks.observe(viewLifecycleOwner, Observer {
            if (it != null && it.isNotEmpty()) {
//                Log.e(TAG, "got tasks $it")
                adapter.swapData(it)
                setAdapterPosition(it) //got new tasks, change position. NoSuchElementException: Collection contains no element matching the predicate.
            } else Log.e(TAG, "got empty or null tasks $it")
//            Log.e(TAG, "got tasks ${it}")
        })

        homeViewModel.error.observe(viewLifecycleOwner, Observer {
            if (it != null && it.isNotEmpty()) homeBinding.textHome.text = it.toString()
        })

        if (!homeViewModel.loggedIn()) findNavController().navigate(R.id.nav_login)
        // else homeViewModel.getTasks()
        homeViewModel.refreshTasks()
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // recyclerview init
        homeBinding.tasksRecycler.initWithLinLay(
            LinearLayoutManager.VERTICAL,
            adapter,
            listOf()
        )
        homeBinding.tasksRecycler.addItemDivider()
    }


    private fun onTabSelectedListener(): OnTabSelectedListener {
        return object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.text) {
                    getString(R.string.pending )-> homeViewModel.filterPending()
                    getString(R.string.running )-> homeViewModel.filterRunning()
                    getString(R.string.completed )-> homeViewModel.filterCompleted()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        }
    }

    private fun setAdapterPosition(it: List<TaskWithActivities>) {
        if (homeViewModel.getVisibleTaskId() != 0) {
            try {
                val task = it.first { taskWithActivities -> taskWithActivities.taskEntity.taskId == homeViewModel.getVisibleTaskId() }
                homeBinding.tasksRecycler.scrollToPosition(it.indexOf(task)) //setting position of current task, if it exist
            }catch (e : NoSuchElementException){
                homeBinding.tasksRecycler.scrollToPosition(0) //setting position of first task, if current not exist
            }
        } else homeViewModel.setVisibleTaskIDs(it[0]) //else on create set id of first element.
    }

    override fun onLazyItemClick(data: TaskWithActivities) {
        Log.e(TAG, "got click taskid " + data.taskEntity.taskId)
    }
}
