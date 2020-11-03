package com.abona_erp.driverapp.ui.fdanger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.model.DangerousGoods
import com.abona_erp.driverapp.databinding.DangerousGoodsFragmentBinding
import com.abona_erp.driverapp.ui.utils.JsonParser
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class DangerousGoodsFragment : Fragment() {



    val TAG = "DangerousGoodsFragment"

    private val mapViewModel by viewModels<DangerousGoodsViewModel>()

    private lateinit var dangerFragmentBinding: DangerousGoodsFragmentBinding

    val args: DangerousGoodsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dangerous_goods_fragment, container, false)
        dangerFragmentBinding = DangerousGoodsFragmentBinding.bind(view).apply {
            viewmodel = mapViewModel
        }

        dangerFragmentBinding.lifecycleOwner = this.viewLifecycleOwner

        val data =  args.goodsData

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
                row.findViewById<TextView>(R.id.txt_item_row).text =  "${entry.key} ${entry.value}"
                linearContent.addView(row)
            }
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}