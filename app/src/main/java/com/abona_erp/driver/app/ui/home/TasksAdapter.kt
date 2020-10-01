package com.abona_erp.driver.app.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.abona_erp.driver.app.R
import com.abona_erp.driver.app.data.local.db.ConfirmationType
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.abona_erp.driver.app.databinding.TaskItemBinding
import com.abona_erp.driver.app.ui.utils.JsonParser
import com.google.gson.Gson
import com.kivi.remote.presentation.base.recycler.LazyAdapter
import org.json.JSONObject


class TasksAdapter(itemClickListener: HomeFragment?) : LazyAdapter<TaskEntity, TaskItemBinding>(null) {
val TAG = "TasksAdapter"
    override fun bindData(data: TaskEntity, binding: TaskItemBinding) {
//        binding.progressBar.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
        binding.root.setOnClickListener { _ ->
            Log.e("TasksAdapter", "got click")
        }

        val jsonObject = JSONObject(Gson().toJson(data).trim())
        val map: Map<String, String> = JsonParser.parseJson(jsonObject)

        val linearContent : LinearLayout = binding.linlayDescription
        if (linearContent.childCount > 0)linearContent.removeAllViews()
        linearContent.setBackgroundColor(ConfirmationType.getColor(binding.root.context, data.confirmationType))
        linearContent.setOnClickListener { itemClickListener?.onLazyItemClick(data)}

        map.entries.forEach { entry ->
            run {
                val row = LayoutInflater.from(binding.root.context)
                    .inflate(R.layout.task_row, null, false)
                row.findViewById<TextView>(R.id.txt_item_row).text = entry.key + " " + entry.value
             //   Log.d(TAG, " adding entry "+ entry.key + " " + entry.value)
                linearContent.addView(row)
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.task_item
}