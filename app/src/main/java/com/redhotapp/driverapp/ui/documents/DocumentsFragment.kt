package com.redhotapp.driverapp.ui.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.databinding.DocumentsFragmentBinding
import com.redhotapp.driverapp.databinding.LoginFragmentBinding
import com.redhotapp.driverapp.ui.base.BaseFragment
import com.redhotapp.driverapp.ui.delayReason.DelayReasonViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DocumentsFragment : BaseFragment() {
    private val docViewModel by viewModels<DocumentsViewModel> ()

    private lateinit var docBinding: DocumentsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.documents_fragment, container, false)

        docBinding = DocumentsFragmentBinding.bind(view).apply {
            viewmodel = docViewModel
        }

        docBinding.lifecycleOwner = this.viewLifecycleOwner
        return view

    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//    }

//    companion object {
//        fun newInstance(): DocumentsFragment {
//            return DocumentsFragment()
//        }
//    }
}