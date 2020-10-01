package com.abona_erp.driver.app.ui.activities

import android.os.Bundle
import android.util.Log
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
import com.abona_erp.driver.app.R
import com.abona_erp.driver.app.data.Constant
import com.abona_erp.driver.app.data.local.db.ActivityEntity
import com.abona_erp.driver.app.databinding.DriverActFragmentBinding
import com.abona_erp.driver.app.ui.base.BaseFragment
import com.abona_erp.driver.app.ui.utils.DeviceUtils
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


        driverActViewModel.getActivityObservable().observe(viewLifecycleOwner, Observer {
            if (it != null && it.isNotEmpty()) {
                adapter.swapData(it)
            }
        })

        driverActViewModel.error.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) driverActFragmentBinding.textHome.text = it.toString()
        })


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
       Log.d(TAG, " on activity click : ${data.activityId}")
    }
}