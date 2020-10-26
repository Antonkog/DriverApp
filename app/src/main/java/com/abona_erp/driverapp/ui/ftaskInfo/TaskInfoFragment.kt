package com.abona_erp.driverapp.ui.ftaskInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.TaskEntity
import com.abona_erp.driverapp.databinding.TaskInfoFragmentBinding
import com.abona_erp.driverapp.ui.utils.JsonParser
import com.google.gson.Gson
import org.json.JSONObject

class TaskInfoFragment : Fragment() {

    val TAG = "TaskInfoFragment"

    private val taskInfoViewModel by viewModels<TaskInfoViewModel>()

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
        val linearContent: LinearLayout = taskInfoFragmentBinding.infoContainer
        /*

        /*
{
  "actionType": "1",
  "activityIds": [
    15,
    16,
    17
  ],
  "address": {
    "City": "Kharkiv",
    "Latitude": 0,
    "Longitude": 0,
    "Name1": "medical goods",
    "Nation": "UA",
    "Street": "Test4 str",
    "ZIP": "11500"
  },
  "changeReason": 0,
  "confirmationType": "RECEIVED",
  "constacts": [
    {
      "ContactType": 0,
      "Number": "004915114744063",
      "NumberType": 0
    }
  ],
  "dangerousGoods": {
    "ADRClass": 0,
    "DangerousGoodsClassType": 0,
    "IsGoodsDangerous": true,
    "UNNo": ""
  },
  "kundenName": "ANTON KOGAN TEST4",
  "mandantId": 3,
  "orderDetails": {
    "CustomerName": "ANTON KOGAN TEST4",
    "CustomerNo": 60238,
    "OrderNo": 202040165,
    "ReferenceIdCustomer1": "4321"
  },
  "palletExchange": {
    "ExchangeType": 0,
    "IsDPL": false,
    "PalletsAmount": -1
  },
  "status": "0",
  "taskDueDateFinish": "2020-10-06T00:00:00",
  "taskDueDateStart": "0001-01-01T00:00:00",
  "taskId": 9961
}         */
         */
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
                    .inflate(R.layout.task_row, null, false)
                row.findViewById<TextView>(R.id.txt_item_row).text = entry.key + " " + entry.value
                linearContent.addView(row)
            }
        }
    }

}