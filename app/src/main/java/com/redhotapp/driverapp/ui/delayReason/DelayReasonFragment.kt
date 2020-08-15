package com.redhotapp.driverapp.ui.delayReason

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.databinding.DelayReasonFragmentBinding
import com.redhotapp.driverapp.databinding.DocumentsFragmentBinding
import com.redhotapp.driverapp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DelayReasonFragment : BaseFragment() {
    private val delayViewModel by viewModels<DelayReasonViewModel> ()

    private lateinit var delayBinding: DelayReasonFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.delay_reason_fragment, container, false)

        delayBinding = DelayReasonFragmentBinding.bind(view).apply {
            viewmodel = delayViewModel
        }

        delayBinding.lifecycleOwner = this.viewLifecycleOwner
        return view
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//    }

//    companion object {
//        fun newInstance(): DelayReasonFragment {
//            return DelayReasonFragment()
//        }
//    }
}