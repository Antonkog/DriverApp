package com.redhotapp.driverapp.ui.home

import android.net.nsd.NsdServiceInfo
import androidx.core.content.res.ResourcesCompat
import com.kivi.remote.presentation.base.recycler.LazyAdapter
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.data.model.AllTask
import com.redhotapp.driverapp.data.model.abona.TaskItem
import com.redhotapp.driverapp.databinding.TaskItemBinding

class TasksAdapter(itemClickListener: HomeFragment) : LazyAdapter<AllTask, TaskItemBinding>(itemClickListener) {

    override fun bindData(data: AllTask, binding: TaskItemBinding) {
        binding.root.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
        binding.taskText.text = "task: $data"
    }

    override fun getLayoutId(): Int = R.layout.task_item
}