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
import com.abona_erp.driverapp.data.model.Contact
import com.abona_erp.driverapp.databinding.ContactsFragmentBinding
import com.abona_erp.driverapp.ui.utils.JsonParser
import com.google.gson.Gson
import org.json.JSONObject

class ContactsDialog : DialogFragment() {

    val TAG = "ContactsFragment"

    private lateinit var contactsFragmentBinding: ContactsFragmentBinding

    val args: ContactsDialogArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.contacts_fragment, container, false)

        contactsFragmentBinding = ContactsFragmentBinding.bind(view)

        contactsFragmentBinding.lifecycleOwner = this.viewLifecycleOwner

        val data = args.contactsData
        if (data != null)
            addTestRows(data.toList())
        else {
            Toast.makeText(context, getString(R.string.error_no_contacts), Toast.LENGTH_SHORT)
                .show()
        }
        return view

    }


    private fun addTestRows(contacts: List<Contact>) {
        contacts.forEach {
            val contact = it

            val jsonObject = JSONObject(Gson().toJson(contact).trim())

            val linearContent: LinearLayout = contactsFragmentBinding.infoContainer

            val map: HashMap<String, String> = java.util.HashMap()
            JsonParser.parseJson(jsonObject, map)

            map.entries.forEach { entry ->
                run {
                    val row = LayoutInflater.from(contactsFragmentBinding.root.context)
                        .inflate(R.layout.parsed_json_row, null, false)
                    row.findViewById<TextView>(R.id.txt_item_row).text = "${entry.key} ${entry.value}"
                    linearContent.addView(row)
                }
            }

        }

    }

}