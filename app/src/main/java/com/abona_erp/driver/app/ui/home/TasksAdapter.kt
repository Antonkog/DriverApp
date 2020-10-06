package com.abona_erp.driver.app.ui.home

import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.abona_erp.driver.app.R
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.abona_erp.driver.app.databinding.TaskItemBinding
import com.kivi.remote.presentation.base.recycler.LazyAdapter


class TasksAdapter(itemClickListener: HomeFragment) : LazyAdapter<TaskEntity, TaskItemBinding>(itemClickListener) {
val TAG = "TasksAdapter"
    override fun bindData(data: TaskEntity, binding: TaskItemBinding) {
     binding.textTaskName.text = data.actionType.name
     binding.textOrderNo.text = ""+ data.orderDetails?.orderNo
     binding.textFinishTime.text = data.taskDueDateFinish
    }

    override fun getLayoutId(): Int = R.layout.task_item
}