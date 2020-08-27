package com.redhotapp.driverapp.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kivi.remote.presentation.base.recycler.LazyAdapter
import com.kivi.remote.presentation.base.recycler.initWithLinLay
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.data.local.db.ActivityEntity
import com.redhotapp.driverapp.data.model.Activity
import com.redhotapp.driverapp.databinding.DriverActFragmentBinding
import com.redhotapp.driverapp.ui.base.BaseFragment
import com.redhotapp.driverapp.ui.utils.DeviceUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverActFragment : BaseFragment(), LazyAdapter.OnItemClickListener<ActivityEntity> {
    val TAG = "DriverActFragment"

    private val driverActViewModel by viewModels<DriverActViewModel>()

    private lateinit var driverActFragmentBinding: DriverActFragmentBinding

    private var adapter = ActivityAdapter(this)

//    val args: DriverActFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        driverActFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.driver_act_fragment, container,
            false
        )

        driverActFragmentBinding.viewmodel = driverActViewModel
        driverActFragmentBinding.lifecycleOwner = this.viewLifecycleOwner


        driverActViewModel.mutableTasks.observe(viewLifecycleOwner, Observer {
            if (it != null && it.isNotEmpty()) {
                adapter.swapData(it)
//                Log.e(TAG, "got tasks")
            }
        })

        driverActViewModel.error.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) driverActFragmentBinding.textHome.text = it.toString()
        })

        driverActViewModel.getActivities(DeviceUtils.getUniqueID(context))

//        if(args ==null) driverActViewModel.populateActivities(DeviceUtils.getUniqueID(context))
//        else driverActViewModel.populateActivities(DeviceUtils.getUniqueID(context), args.taskId)

        return driverActFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // recyclerview init
        driverActFragmentBinding.tasksRecycler.initWithLinLay(
            LinearLayoutManager.VERTICAL,
            adapter,
            listOf()
        )
//        driverActFragmentBinding.tasksRecycler.addItemDivider()

    }

    override fun onLazyItemClick(data: ActivityEntity) {
        Toast.makeText(context, " on activity click : ${data.activityId}", Toast.LENGTH_LONG).show()
    }
}