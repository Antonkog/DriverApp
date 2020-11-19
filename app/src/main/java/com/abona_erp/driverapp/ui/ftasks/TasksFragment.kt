package com.abona_erp.driverapp.ui.ftasks

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.ConfirmationType
import com.abona_erp.driverapp.databinding.TasksFragmentBinding
import com.abona_erp.driverapp.ui.base.BaseFragment
import com.abona_erp.driverapp.ui.utils.UtilModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.action_refresh).let {
            it?.setVisible(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                tasksViewModel.refreshTasks()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
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
        tasksViewModel.tasks.observe(viewLifecycleOwner, {
            tasksViewModel.setTasks(it)
        })

        tasksViewModel.filteredTasks.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                adapter.swapData(it)
                setAdapterPosition(it) //got new tasks, change position. NoSuchElementException: Collection contains no element matching the predicate.
                Log.d(TAG, "got tasks ${it.size}")
            } else {
                adapter.swapData(listOf())
                Log.e(TAG, "got empty or null tasks $it")
            }
//            Log.e(TAG, "got tasks ${it}")
        })


        if (!tasksViewModel.loggedIn()) {
            Log.e(TAG, "  not logged in ")
            findNavController().navigate(TasksFragmentDirections.actionNavHomeToLoginFragment())
        }
        when (tasksViewModel.tabStatus) {
            TasksViewModel.TabStatus.TO_DO -> {
                tasksBinding.tabLayout.getTabAt(TasksViewModel.TabStatus.TO_DO.ordinal)?.select()
            }
            TasksViewModel.TabStatus.COMPLETED -> {
                tasksBinding.tabLayout.getTabAt(TasksViewModel.TabStatus.COMPLETED.ordinal)
                    ?.select()
            }
        }
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
                when (tab?.text) {
                    getString(R.string.to_do) -> tasksViewModel.filterTodo()
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
        val confirmationState = data.taskEntity.confirmationType
        if (confirmationState < ConfirmationType.TASK_CONFIRMED_BY_USER) {// old starte - not opened and not confirmed
            context?.let {
                val name =
                    it.resources.getString(UtilModel.getResIdByTaskActionType(data.taskEntity))
                DialogBuilder.getStartTaskDialog(
                    name,
                    { _, _ ->
                        tasksViewModel.confirmTask(taskEntity = data.taskEntity)
                    }, it
                ).show()
            }
        } else { //here is ui update if server confirmed - can open task.
            tasksViewModel.updateTask(data.taskEntity.copy(openCondition = !data.taskEntity.openCondition))
        }
        Log.e(TAG, "got click taskid " + data.taskEntity.taskId)
    }
}
