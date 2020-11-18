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
import com.abona_erp.driverapp.databinding.DangerousGoodsFragmentBinding
import com.abona_erp.driverapp.databinding.DialogDelayReasonBinding
import com.abona_erp.driverapp.databinding.DialogDelayReasonBindingImpl
import com.abona_erp.driverapp.databinding.DriverActFragmentBinding
import com.abona_erp.driverapp.ui.factivities.DriverActFragmentArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DelayReasonDialog : DialogFragment() {

    val args: DelayReasonDialogArgs by navArgs()

     lateinit var dialogDelayReasonBinding: DialogDelayReasonBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =  inflater.inflate(
            R.layout.dialog_delay_reason,
            container, false)
            dialogDelayReasonBinding = DialogDelayReasonBinding.bind(root)
          //  dialogDelayReasonBinding.button.setOnClickListener {it}


        val sources = arrayOf(
            resources.getString(R.string.delay_na),
            resources.getString(R.string.delay_customer),
            resources.getString(R.string.delay_dispatcher),
            resources.getString(R.string.delay_driver)
        )

        dialogDelayReasonBinding.sourcePicker.maxValue = sources.size -1
        dialogDelayReasonBinding.sourcePicker.displayedValues = sources


        val data = args.activityEntity

        val reasons = arrayListOf<String>()
        data?.delayReasons?.forEach {
            if(it.translatedReasonText!= null)
            reasons.add(it.translatedReasonText)
            else if(it.reasonText != null) reasons.add(it.reasonText)
        }
        if(reasons.isNotEmpty()){
            dialogDelayReasonBinding.reasonPicker.displayedValues = reasons.toTypedArray()
        } else{
            dialogDelayReasonBinding.button.visibility = View.GONE
            dialogDelayReasonBinding.reasonPicker.displayedValues = arrayOf(getString(R.string.no_delay_reasons))
        }
        return root
    }
}