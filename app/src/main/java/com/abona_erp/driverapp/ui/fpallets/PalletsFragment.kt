package com.abona_erp.driverapp.ui.fpallets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.model.NotesItem
import com.abona_erp.driverapp.data.model.PalletExchange
import com.abona_erp.driverapp.databinding.PalletsFragmentBinding
import com.abona_erp.driverapp.ui.base.BaseFragment
import com.abona_erp.driverapp.ui.utils.JsonParser
import com.google.gson.Gson
import org.json.JSONObject

class PalletsFragment : BaseFragment() {


    companion object {
        const val TAG = "PalletsFragment"
    }


    private val notesViewModel by viewModels<PalletsViewModel>()
    private lateinit var notesBinding: PalletsFragmentBinding

    val args: PalletsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.pallets_fragment, container, false)

        notesBinding = PalletsFragmentBinding.bind(view).apply {
            viewmodel = notesViewModel
        }

        notesBinding.lifecycleOwner = this.viewLifecycleOwner

        val data = args.palletsData
        if (data != null)
            addTestRows(data)
        else {
            Toast.makeText(context, getString(R.string.error_pallets_not_found), Toast.LENGTH_SHORT)
                .show()
        }
        return view

    }


    private fun addTestRows(item: PalletExchange) {
        val jsonObject = JSONObject(Gson().toJson(item).trim())


        val linearContent: LinearLayout = notesBinding.infoContainer

        val map: HashMap<String, String> = java.util.HashMap()
        JsonParser.parseJson(jsonObject, map)

        map.entries.forEach { entry ->
            run {
                val row = LayoutInflater.from(notesBinding.root.context)
                    .inflate(R.layout.parsed_json_row, null, false)
                row.findViewById<TextView>(R.id.txt_item_row).text = "${entry.key} ${entry.value}"
                linearContent.addView(row)
            }
        }
    }

}