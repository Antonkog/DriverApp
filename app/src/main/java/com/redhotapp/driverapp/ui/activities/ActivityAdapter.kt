package com.redhotapp.driverapp.ui.activities

import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import com.kivi.remote.presentation.base.recycler.LazyAdapter
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.data.local.db.ActivityEntity
import com.redhotapp.driverapp.data.model.Activity
import com.redhotapp.driverapp.databinding.ActivityItemBinding
import com.redhotapp.driverapp.ui.utils.JsonParser
import org.json.JSONObject


class ActivityAdapter(itemClickListener: DriverActFragment) :
    LazyAdapter<ActivityEntity, ActivityItemBinding>(itemClickListener) {

    override fun bindData(data: ActivityEntity, binding: ActivityItemBinding) {
        binding.root.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
        val jsonObject = JSONObject(Gson().toJson(data).trim())
        val map: Map<String, String> = JsonParser.parseJson(jsonObject)

        val linearContent: LinearLayout = binding.linlayDescription

        map.entries.forEach { entry ->
            run {
                val row = LayoutInflater.from(binding.root.context)
                    .inflate(R.layout.task_row, null, false)
                row.findViewById<TextView>(R.id.txt_item_row).text = entry.key + " " + entry.value
                linearContent.addView(row)
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_item
}