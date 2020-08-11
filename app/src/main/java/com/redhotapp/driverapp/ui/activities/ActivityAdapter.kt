package com.redhotapp.driverapp.ui.activities

import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import com.kivi.remote.presentation.base.recycler.LazyAdapter
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.data.model.Activity
import com.redhotapp.driverapp.data.model.AllTask
import com.redhotapp.driverapp.databinding.TaskItemBinding
import com.redhotapp.driverapp.ui.home.HomeFragment
import com.redhotapp.driverapp.ui.utils.JsonParser
import org.json.JSONObject


class ActivityAdapter(itemClickListener: DriverActFragment) : LazyAdapter<Activity, TaskItemBinding>(itemClickListener) {

    override fun bindData(data: Activity, binding: TaskItemBinding) {
        binding.root.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
//        binding.progressBar.progress = if(data.status > 100) 100 else (data.status)   //text = "task: $data"
        binding.progressBar.progress = 100
        val jsonObject = JSONObject(Gson().toJson(data).trim())
        val map: Map<String, String> = JsonParser.parseJson(jsonObject)

        val linearContent : LinearLayout = binding.linlayDescription

        map.entries.forEach { entry ->
            run {
                val row = LayoutInflater.from(binding.root.context)
                    .inflate(R.layout.task_row, null, false)
                row.findViewById<TextView>(R.id.txt_item_row).text = entry.key + " " + entry.value
                linearContent.addView(row)
            }
        }



/*
LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
View v = vi.inflate(R.layout.your_layout, null);

// fill in any details dynamically here
TextView textView = (TextView) v.findViewById(R.id.a_text_view);
textView.setText("your text");

// insert into main view
ViewGroup insertPoint = (ViewGroup) findViewById(R.id.insert_point);
insertPoint.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
 */

//        "MandantId":3,
//        "MandantName":"Hegelmann Express GmbH",
//        "TaskId":1213,
//        "TaskChangeId":2331,
//        "AbonaTransferNr":"",
//        "PreviousTaskId":0,
//        "NextTaskId":1215,
//        "VehiclePreviousTaskId":0,
//        "VehicleNextTaskId":0,
//        "ChangeReason":1,
//        "ActionType":0,
//        "OrderNo":202023123,
//        "Description":null,
//        "KundenName":"Amazon",
//        "KundenNr":30118,
//        "ReferenceIdCustomer1":"a",
//        "ReferenceIdCustomer2":null,
//        "PalletsAmount":0,
//        "TaskDueDateStart":"0001-01-01T00:00:00",
//        "TaskDueDateFinish":"2020-06-03T00:00:00",
//        "Status":100,
//        "PercentFinishedActivities":0,
//
    }

    override fun getLayoutId(): Int = R.layout.task_item
}