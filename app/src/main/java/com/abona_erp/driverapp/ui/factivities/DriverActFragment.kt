package com.abona_erp.driverapp.ui.factivities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.ActivityWrapper
import com.abona_erp.driverapp.databinding.DriverActFragmentBinding
import com.abona_erp.driverapp.ui.base.BaseFragment
import com.kivi.remote.presentation.base.recycler.LazyAdapter
import com.kivi.remote.presentation.base.recycler.initWithLinLay
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverActFragment : BaseFragment(), LazyAdapter.OnItemClickListener<ActivityWrapper> {
    val TAG = "DriverActFragment"

    private val driverActViewModel by viewModels<DriverActViewModel>()

    private lateinit var driverActFragmentBinding: DriverActFragmentBinding
    private lateinit var adapter: ActivityAdapter


    val args: DriverActFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        driverActFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.driver_act_fragment, container,
            false
        )
        adapter = ActivityAdapter(this, findNavController())

        driverActFragmentBinding.viewmodel = driverActViewModel
        driverActFragmentBinding.lifecycleOwner = viewLifecycleOwner

        args.taskEntity?.taskId?.let { it ->
            driverActViewModel.getActivityObservable(it).observe(viewLifecycleOwner) { activities->
                if (activities.isNotEmpty()) {
                    driverActViewModel.wrapActivities(activities)
                }
            }
        }

        driverActViewModel.wrappedActivities.observe(viewLifecycleOwner) {
            adapter.swapData(it)
        }


//        if(args ==null) driverActViewModel.populateActivities(DeviceUtils.getUniqueID(context))
//        else driverActViewModel.populateActivities(DeviceUtils.getUniqueID(context), args.taskId)

        return driverActFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // recyclerview init
        driverActFragmentBinding.activityRecycler.initWithLinLay(
            LinearLayoutManager.VERTICAL,
            adapter,
            listOf()
        )
//        driverActFragmentBinding.tasksRecycler.addItemDivider()

    }

    override fun onLazyItemClick(data: ActivityWrapper) {
        Log.d(TAG, " on activity click : ${data.activity.activityId}")
        driverActViewModel.checkTimeAndPostActivity(data.activity)
    }
}