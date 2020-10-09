package com.abona_erp.driver.app.ui.activities

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import com.kivi.remote.presentation.base.recycler.LazyAdapter
import com.abona_erp.driver.app.R
import com.abona_erp.driver.app.data.local.db.ActivityEntity
import com.abona_erp.driver.app.data.model.Activity
import com.abona_erp.driver.app.data.model.ActivityStatus
import com.abona_erp.driver.app.databinding.ActivityItemBinding
import com.abona_erp.driver.app.ui.utils.JsonParser
import org.json.JSONObject


class ActivityAdapter(itemClickListener: DriverActFragment) :
    LazyAdapter<ActivityEntity, ActivityItemBinding>(itemClickListener) {

    override fun bindData(data: ActivityEntity, binding: ActivityItemBinding) {

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

        when(data.activityStatus){
            ActivityStatus.PENDING -> binding.buttonActConfirm.text = binding.root.resources.getString(R.string.activity_start)
            ActivityStatus.RUNNING -> binding.buttonActConfirm.text =  binding.root.resources.getString(R.string.next)
            ActivityStatus.FINISHED -> binding.buttonActConfirm.visibility = View.GONE
        }

        binding.buttonActConfirm.setOnClickListener {
            itemClickListener?.onLazyItemClick(data)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_item
}