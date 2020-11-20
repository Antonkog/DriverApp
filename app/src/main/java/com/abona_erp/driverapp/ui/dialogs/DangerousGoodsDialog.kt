package com.abona_erp.driverapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.model.DangerousGoods
import com.abona_erp.driverapp.databinding.DangerousGoodsFragmentBinding
import com.abona_erp.driverapp.ui.utils.JsonParser
import com.google.gson.Gson
import org.json.JSONObject

class DangerousGoodsDialog : DialogFragment() {


    val TAG = "DangerousGoodsFragment"


    private lateinit var dangerFragmentBinding: DangerousGoodsFragmentBinding

    val args: DangerousGoodsDialogArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dangerous_goods_fragment, container, false)
        dangerFragmentBinding = DangerousGoodsFragmentBinding.bind(view)

        dangerFragmentBinding.lifecycleOwner = this.viewLifecycleOwner

        val data = args.goodsData

        addTestRows(data)
        return view

    }


    private fun addTestRows(dangerGoods: DangerousGoods) {
        val jsonObject = JSONObject(Gson().toJson(dangerGoods).trim())


        val linearContent: LinearLayout = dangerFragmentBinding.infoContainer

        val map: HashMap<String, String> = java.util.HashMap()
        JsonParser.parseJson(jsonObject, map)

        map.entries.forEach { entry ->
            run {
                val row = LayoutInflater.from(dangerFragmentBinding.root.context)
                    .inflate(R.layout.parsed_json_row, null, false)
                row.findViewById<TextView>(R.id.txt_item_row).text = "${entry.key} ${entry.value}"
                linearContent.addView(row)
            }
        }
    }


}