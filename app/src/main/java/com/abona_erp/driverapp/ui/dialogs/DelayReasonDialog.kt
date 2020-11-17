package com.abona_erp.driverapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.databinding.DriverActFragmentBinding
import com.abona_erp.driverapp.ui.factivities.DriverActFragmentArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DelayReasonDialog : DialogFragment() {

    val args: DelayReasonDialogArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =  inflater.inflate(
            R.layout.dialog_delay_reason,
            container, false)


        root.findViewById<TextView>(R.id.textView).text = args.activityEntity.toString()
        return root
    }
}