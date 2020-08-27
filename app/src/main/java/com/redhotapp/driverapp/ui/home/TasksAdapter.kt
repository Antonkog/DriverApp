package com.redhotapp.driverapp.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import com.kivi.remote.presentation.base.recycler.LazyAdapter
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.data.local.db.TaskEntity
import com.redhotapp.driverapp.data.model.AllTask
import com.redhotapp.driverapp.databinding.TaskItemBinding
import com.redhotapp.driverapp.ui.utils.JsonParser
import org.json.JSONObject


class TasksAdapter(itemClickListener: HomeFragment?) : LazyAdapter<TaskEntity, TaskItemBinding>(null) {
val TAG = "TasksAdapter"
    override fun bindData(data: TaskEntity, binding: TaskItemBinding) {
//        binding.progressBar.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
        binding.root.setOnClickListener { v ->
            Log.e("TasksAdapter", "got click")
        }

        val jsonObject = JSONObject(Gson().toJson(data).trim())
        val map: Map<String, String> = JsonParser.parseJson(jsonObject)

        val linearContent : LinearLayout = binding.linlayDescription

        map.entries.forEach { entry ->
            run {
                val row = LayoutInflater.from(binding.root.context)
                    .inflate(R.layout.task_row, null, false)
                row.findViewById<TextView>(R.id.txt_item_row).text = entry.key + " " + entry.value
//                Log.e(TAG, " adding entry "+ entry.key + " " + entry.value)
                linearContent.addView(row)
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.task_item
}