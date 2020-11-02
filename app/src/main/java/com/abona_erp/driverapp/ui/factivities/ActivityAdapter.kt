package com.abona_erp.driverapp.ui.factivities

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.ActivityEntity
import com.abona_erp.driverapp.data.local.db.ActivityWrapper
import com.abona_erp.driverapp.data.model.ActivityStatus
import com.abona_erp.driverapp.databinding.ActivityItemBinding
import com.abona_erp.driverapp.ui.utils.JsonParser
import com.google.gson.Gson
import com.kivi.remote.presentation.base.recycler.LazyAdapter
import org.json.JSONObject


class ActivityAdapter(itemClickListener: DriverActFragment) :
    LazyAdapter<ActivityWrapper, ActivityItemBinding>(itemClickListener) {

    override fun bindData(data: ActivityWrapper, binding: ActivityItemBinding) {

        binding.textActName.text = data.activity.name


        if(data.buttonVisible) binding.buttonActConfirm.visibility = View.VISIBLE
        else binding.buttonActConfirm.visibility = View.GONE

        when (data.activity.activityStatus) {
            ActivityStatus.PENDING -> {
                binding.textFinished.visibility = View.INVISIBLE
                binding.buttonActConfirm.text =
                    binding.root.resources.getString(R.string.activity_start)
            }
            ActivityStatus.RUNNING -> {
                binding.textFinished.visibility = View.INVISIBLE
                binding.buttonActConfirm.text =
                    binding.root.resources.getString(R.string.activity_next)
            }
            ActivityStatus.FINISHED -> {
                binding.buttonActConfirm.visibility = View.GONE
                binding.textFinishedValue.text = data.activity.finished
            }
            ActivityStatus.ENUM_ERROR -> {
                binding.textFinished.visibility = View.INVISIBLE
                binding.buttonActConfirm.visibility = View.GONE
            }
        }

        if(data.isLastActivity){
            binding.buttonActConfirm.text =
                binding.root.resources.getString(R.string.activity_finish)
        }


        binding.textStartedValue.text = data.activity.started

        binding.buttonActConfirm.setOnClickListener {
            itemClickListener?.onLazyItemClick(data)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_item
}