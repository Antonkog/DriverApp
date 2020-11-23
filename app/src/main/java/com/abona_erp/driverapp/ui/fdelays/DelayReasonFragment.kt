package com.abona_erp.driverapp.ui.fdelays

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.DelayReasonEntity
import com.abona_erp.driverapp.data.model.DelaySource
import com.abona_erp.driverapp.databinding.DelayReasonFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DelayReasonFragment : DialogFragment() {

    val args: DelayReasonFragmentArgs by navArgs()

    private val delayViewModel by viewModels<DelayReasonViewModel>()

    lateinit var dialogDelayReasonBinding: DelayReasonFragmentBinding

    var reasons = listOf<DelayReasonEntity>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =  inflater.inflate(
            R.layout.delay_reason_fragment,
            container, false)
            dialogDelayReasonBinding = DelayReasonFragmentBinding.bind(root).apply {
                viewmodel = delayViewModel
                lifecycleOwner = this.lifecycleOwner
            }


        val sources = arrayOf(
            resources.getString(R.string.delay_na),
            resources.getString(R.string.delay_customer),
            resources.getString(R.string.delay_dispatcher),
            resources.getString(R.string.delay_driver)
        )

        dialogDelayReasonBinding.sourcePicker.maxValue = sources.size -1
        dialogDelayReasonBinding.sourcePicker.displayedValues = sources
        dialogDelayReasonBinding.sourcePicker.value = DelaySource.DRIVER.ordinal

        val data = args.activityEntity

        delayViewModel.delayReasons.observe(viewLifecycleOwner, { it ->
         reasons =    it.filter { it.activityId == data?.activityId }
            if(reasons.isNotEmpty()){
                dialogDelayReasonBinding.reasonPicker.maxValue = reasons.size -1
                dialogDelayReasonBinding.reasonPicker.displayedValues = reasons.map {it.reasonText}.toTypedArray()
            } else{
                dialogDelayReasonBinding.button.visibility = View.GONE
                dialogDelayReasonBinding.reasonPicker.displayedValues = arrayOf(getString(R.string.no_delay_reason))
            }
        })



        if(data?.taskpId != null && data?.mandantId != null)
        dialogDelayReasonBinding.button.setOnClickListener {
            val current  = reasons[dialogDelayReasonBinding.reasonPicker.value].copy(
                mandantId = data.mandantId,
                taskId = data.taskpId,
                timestampUtc = System.currentTimeMillis(),
                delayInMinutes = dialogDelayReasonBinding.rangedTimePicker.getMinutes(),
                delaySource = DelaySource.getDelaySourceByCode(dialogDelayReasonBinding.sourcePicker.value))
            if (checkData(dialogDelayReasonBinding)) sendDelay(current)
            else showWrongData()
        }

        delayViewModel.goBack.observe(viewLifecycleOwner, {
           if(it == true) findNavController().popBackStack()
        })

        return root
    }


    private fun showWrongData() {
       Toast.makeText(context, getString(R.string.set_all_data), Toast.LENGTH_SHORT).show()
    }

    private fun sendDelay(delayReasonEntity: DelayReasonEntity) {
        val newAct =  args.activityEntity?.copy(delayReasons =  arrayListOf(delayReasonEntity))
        if(newAct!=null){
            delayViewModel.postDelayReason(newAct)
        } else{
            Toast.makeText(context, "error while posting delay", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkData(dialogDelayReasonBinding:  DelayReasonFragmentBinding): Boolean {
        return  (dialogDelayReasonBinding.sourcePicker.value !=0 ||
            dialogDelayReasonBinding.rangedTimePicker.getMinutes() != 0 ||
                    dialogDelayReasonBinding.editReasonText.text.isNotBlank()
        )
    }
}