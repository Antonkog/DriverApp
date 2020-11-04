package com.abona_erp.driverapp.ui.ftasks

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
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.databinding.TasksFragmentBinding
import com.abona_erp.driverapp.ui.base.BaseFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.kivi.remote.presentation.base.recycler.LazyAdapter
import com.kivi.remote.presentation.base.recycler.initWithLinLay
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TasksFragment : BaseFragment(), LazyAdapter.OnItemClickListener<TaskWithActivities> {

    val TAG: String = "TasksFragment"
    private val tasksViewModel by viewModels<TasksViewModel>()

    //    val homeViewModel: HomeViewModel by navGraphViewModels(R.id.nav_home)
    private lateinit var adapter: TasksAdapter

    private lateinit var tasksBinding: TasksFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.tasks_fragment, container, false)

        tasksBinding = TasksFragmentBinding.bind(view).apply {
            viewmodel = tasksViewModel
        }

        tasksBinding.lifecycleOwner = this.viewLifecycleOwner

        adapter = TasksAdapter(this, findNavController())

        tasksBinding.tasksRecycler.adapter = adapter

        tasksBinding.tasksRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)


                try {
                    val layoutManager =
                        recyclerView.layoutManager as LinearLayoutManager //see initWithLinLay

                    val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                    val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

                    val task = adapter.data[firstVisiblePosition]
                    tasksViewModel.setVisibleTaskIDs(task)
                    Log.d(
                        TAG,
                        "visible task id = ${task.taskEntity.taskId} , first vis pos: $firstVisiblePosition , lastvispos =  $lastVisiblePosition"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

//                val id = adapter.getLastVisibleItemId()
//                Log.e(TAG,
//                    " adapter.getLastVisibleItemId() =  $id"
//                )
            }
        })

        tasksBinding.tabLayout.addOnTabSelectedListener(onTabSelectedListener())
        tasksViewModel.tasks.observe(viewLifecycleOwner, Observer {
            tasksViewModel.setTasks(it)
        })

        tasksViewModel.filteredTasks.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
//                Log.e(TAG, "got tasks $it")
                adapter.swapData(it)
                setAdapterPosition(it) //got new tasks, change position. NoSuchElementException: Collection contains no element matching the predicate.
                Log.e(TAG, "got tasks $it")
            } else {
                adapter.swapData(listOf())
                Log.e(TAG, "got empty or null tasks $it")
            }
//            Log.e(TAG, "got tasks ${it}")
        })


        if (!tasksViewModel.loggedIn()) {
            Log.e(TAG,"  not logged in ")
            findNavController().navigate(TasksFragmentDirections.actionNavHomeToLoginFragment())
        }
//        tasksViewModel.refreshTasks()

        tasksViewModel.filterPending() //assume that we on the first tab after fragment recreate
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // recyclerview init
        tasksBinding.tasksRecycler.initWithLinLay(
            LinearLayoutManager.VERTICAL,
            adapter,
            listOf()
        )
//        homeBinding.tasksRecycler.addItemDivider()
    }


    private fun onTabSelectedListener(): OnTabSelectedListener {
        return object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tasksViewModel.clearVisibleTaskId()
                when (tab?.text) {
                    getString(R.string.pending) -> tasksViewModel.filterPending()
                    getString(R.string.running) -> tasksViewModel.filterRunning()
                    getString(R.string.completed) -> tasksViewModel.filterCompleted()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        }
    }

    private fun setAdapterPosition(it: List<TaskWithActivities>) {
        if (tasksViewModel.getVisibleTaskId() != 0) {
            try {
                val task =
                    it.first { taskWithActivities -> taskWithActivities.taskEntity.taskId == tasksViewModel.getVisibleTaskId() }
                tasksBinding.tasksRecycler.scrollToPosition(it.indexOf(task)) //setting position of current task, if it exist
            } catch (e: NoSuchElementException) {
                tasksBinding.tasksRecycler.scrollToPosition(0) //setting position of first task, if current not exist
            }
        } else tasksViewModel.setVisibleTaskIDs(it[0]) //else on create set id of first element.
    }

    override fun onLazyItemClick(data: TaskWithActivities) {
        Log.e(TAG, "got click taskid " + data.taskEntity.taskId)
    }
}
