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

        val jsonObject = JSONObject(Gson().toJson(data).trim())
        val map: HashMap<String, String> = java.util.HashMap()
        JsonParser.parseJson(jsonObject, map)

        val linearContent: LinearLayout = binding.linlayDescription
        if (linearContent.childCount > 0) linearContent.removeAllViews()
        map.entries.forEach { entry ->
            run {
                val row = LayoutInflater.from(binding.root.context)
                    .inflate(R.layout.task_row, null, false)
                row.findViewById<TextView>(R.id.txt_item_row).text = entry.key + " " + entry.value
                linearContent.addView(row)
            }
        }

        if(data.buttonVisible) binding.buttonActConfirm.visibility = View.VISIBLE
        else binding.buttonActConfirm.visibility = View.GONE

        when (data.activity.activityStatus) {
            ActivityStatus.PENDING -> binding.buttonActConfirm.text =
                binding.root.resources.getString(R.string.activity_start)
            ActivityStatus.RUNNING -> binding.buttonActConfirm.text =
                binding.root.resources.getString(R.string.activity_next)
            ActivityStatus.FINISHED -> binding.buttonActConfirm.visibility = View.GONE
            ActivityStatus.ENUM_ERROR -> binding.buttonActConfirm.visibility = View.GONE
        }

        if(data.isLastActivity) binding.buttonActConfirm.text =
            binding.root.resources.getString(R.string.activity_finish)

        binding.buttonActConfirm.setOnClickListener {
            itemClickListener?.onLazyItemClick(data)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_item
}