package com.abona_erp.driverapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.model.NotesItem
import com.abona_erp.driverapp.databinding.NotesFragmentBinding
import com.abona_erp.driverapp.ui.utils.JsonParser
import com.google.gson.Gson
import org.json.JSONObject

class NotesDialog : DialogFragment() {

    companion object {
        const val TAG = "NotesFragment"
    }


    private lateinit var notesBinding: NotesFragmentBinding

    val args: NotesDialogArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.notes_fragment, container, false)

        notesBinding = NotesFragmentBinding.bind(view)

        notesBinding.lifecycleOwner = this.viewLifecycleOwner

        val data = args.notesData
        if (data != null)
            addTestRows(data)
        else {
            Toast.makeText(context, getString(R.string.error_notes_not_found), Toast.LENGTH_SHORT)
                .show()
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
                row.findViewById<TextView>(R.id.txt_item_row).text = "${entry.key} ${entry.value}"
                linearContent.addView(row)
            }
        }
    }

}