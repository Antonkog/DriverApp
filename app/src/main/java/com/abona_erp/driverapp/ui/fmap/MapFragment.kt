package com.abona_erp.driverapp.ui.fmap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.model.Address
import com.abona_erp.driverapp.databinding.MapFragmentBinding
import com.abona_erp.driverapp.ui.base.BaseFragment
import com.abona_erp.driverapp.ui.utils.JsonParser
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class MapFragment : BaseFragment() {

    val TAG = "MapFragment"

    private val mapViewModel by viewModels<MapViewModel>()
    private lateinit var mapBinding: MapFragmentBinding

    val args: MapFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.map_fragment, container, false)

        mapBinding = MapFragmentBinding.bind(view).apply {
            viewmodel = mapViewModel
        }

        mapBinding.lifecycleOwner = this.viewLifecycleOwner

       val data =  args.mapData
        addTestRows(data)
        return view

    }



    private fun addTestRows(address: Address) {
        val jsonObject = JSONObject(Gson().toJson(address).trim())


        val linearContent: LinearLayout = mapBinding.infoContainer

        val map: HashMap<String, String> = java.util.HashMap()
        JsonParser.parseJson(jsonObject, map)

        map.entries.forEach { entry ->
            run {
                val row = LayoutInflater.from(mapBinding.root.context)
                    .inflate(R.layout.parsed_json_row, null, false)
                row.findViewById<TextView>(R.id.txt_item_row).text = "${entry.key} ${entry.value}"
                linearContent.addView(row)
            }
        }
    }


}