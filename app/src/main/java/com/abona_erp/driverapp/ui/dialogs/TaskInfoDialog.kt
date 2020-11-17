package com.abona_erp.driverapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.TaskEntity
import com.abona_erp.driverapp.databinding.TaskInfoFragmentBinding
import com.abona_erp.driverapp.ui.utils.JsonParser
import com.google.gson.Gson
import org.json.JSONObject

class TaskInfoDialog : DialogFragment() {

    val TAG = "TaskInfoFragment"

    private lateinit var taskInfoFragmentBinding: TaskInfoFragmentBinding

    val args: TaskInfoDialogArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        taskInfoFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.task_info_fragment, container,
            false
        )

        taskInfoFragmentBinding.lifecycleOwner = this.viewLifecycleOwner

        val task = args.taskEntity

        addTestRows(task)

        return taskInfoFragmentBinding.root
    }

    private fun addTestRows(task: TaskEntity) {
        val jsonObject = JSONObject(Gson().toJson(task).trim())
        val map: HashMap<String, String> = java.util.HashMap()
        val linearContent: LinearLayout = taskInfoFragmentBinding.infoContainer
        val include = arrayOf(
            "address",
            "Longitude",
            "Latitude",
            "LoadingOrder",
            "DangerousGoods",
            "ADRClass",
            "UNNo",
            "palletExchange",
            "ExchangeType",
            "PalletsAmount",
            "IsDPL",
            "constacts",
            "Name",
            "Number",
            "taskDetails",
            "ReferenceId1",
            "ReferenceIdCustomer2",
            "Notes"
        )

        JsonParser.parseJson(jsonObject, map, include)

        map.entries.forEach { entry ->
            run {
                val row = LayoutInflater.from(taskInfoFragmentBinding.root.context)
                    .inflate(R.layout.parsed_json_row, null, false)
                row.findViewById<TextView>(R.id.txt_item_row).text = "${entry.key} ${entry.value}"
                linearContent.addView(row)
            }
        }
    }

}