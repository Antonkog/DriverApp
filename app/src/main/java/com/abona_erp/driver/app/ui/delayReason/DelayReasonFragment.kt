package com.abona_erp.driver.app.ui.delayReason

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.abona_erp.driver.app.R
import com.abona_erp.driver.app.databinding.DelayReasonFragmentBinding
import com.abona_erp.driver.app.ui.base.BaseFragment
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