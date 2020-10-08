package com.abona_erp.driver.app.ui.taskInfo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.abona_erp.driver.app.R
import com.abona_erp.driver.app.databinding.TaskInfoFragmentBinding

class TaskInfoFragment : Fragment() {

    val TAG = "TaskInfoFragment"

    private val taskInfoViewModel by viewModels<TaskInfoViewModel> ()

    private lateinit var taskInfoFragmentBinding: TaskInfoFragmentBinding

    val args: TaskInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        taskInfoFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.task_info_fragment, container,
            false
        )

        taskInfoFragmentBinding.viewmodel = taskInfoViewModel
        taskInfoFragmentBinding.lifecycleOwner = this.viewLifecycleOwner

        val task = args.taskEntity

        taskInfoFragmentBinding.textInfo.text = task.toString()
        return taskInfoFragmentBinding.root
    }

}