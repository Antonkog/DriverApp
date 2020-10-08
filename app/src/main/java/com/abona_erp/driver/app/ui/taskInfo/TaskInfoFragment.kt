package com.abona_erp.driver.app.ui.taskInfo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.abona_erp.driver.app.R
import com.abona_erp.driver.app.data.local.db.TaskEntity
import com.abona_erp.driver.app.databinding.TaskInfoFragmentBinding
import com.abona_erp.driver.app.ui.utils.JsonParser
import com.google.gson.Gson
import org.json.JSONObject

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

        addTestRows(task)

        return taskInfoFragmentBinding.root
    }

    private fun addTestRows(task: TaskEntity) {
        val jsonObject = JSONObject(Gson().toJson(task).trim())
        val map: HashMap<String, String> = java.util.HashMap()
        JsonParser.parseJson(jsonObject, map)

        val linearContent: LinearLayout = taskInfoFragmentBinding.infoContainer

        map.entries.forEach { entry ->
            run {
                val row = LayoutInflater.from(taskInfoFragmentBinding.root.context)
                    .inflate(R.layout.task_row, null, false)
                row.findViewById<TextView>(R.id.txt_item_row).text = entry.key + " " + entry.value
                linearContent.addView(row)
            }
        }
    }

}