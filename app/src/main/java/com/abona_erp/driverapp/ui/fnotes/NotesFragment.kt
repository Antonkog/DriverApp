package com.abona_erp.driverapp.ui.fnotes

import android.os.Bundle
import androidx.fragment.app.Fragment
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
import com.abona_erp.driverapp.databinding.NotesFragmentBinding
import com.abona_erp.driverapp.ui.base.BaseFragment
import com.abona_erp.driverapp.ui.utils.JsonParser
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class NotesFragment : BaseFragment() {

    companion object {
        const val TAG = "NotesFragment"
    }


    private val notesViewModel by viewModels<NotesViewModel>()
    private lateinit var notesBinding: NotesFragmentBinding

    val args: NotesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.notes_fragment, container, false)

        notesBinding = NotesFragmentBinding.bind(view).apply {
            viewmodel = notesViewModel
        }

        notesBinding.lifecycleOwner = this.viewLifecycleOwner

        val data =  args.notesData
        if(data!=null)
        addTestRows(data)
        else{
            Toast.makeText(context, getString(R.string.error_notes_not_found), Toast.LENGTH_SHORT).show()
        }
        return view

    }



    private fun addTestRows(item: NotesItem) {
        val jsonObject = JSONObject(Gson().toJson(item).trim())


        val linearContent: LinearLayout = notesBinding.infoContainer

        val map: HashMap<String, String> = java.util.HashMap()
        JsonParser.parseJson(jsonObject, map)

        map.entries.forEach { entry ->
            run {
                val row = LayoutInflater.from(notesBinding.root.context)
                    .inflate(R.layout.parsed_json_row, null, false)
                row.findViewById<TextView>(R.id.txt_item_row).text =  "${entry.key} ${entry.value}"
                linearContent.addView(row)
            }
        }
    }

}