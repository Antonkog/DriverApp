package com.redhotapp.driverapp.ui.documents

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kivi.remote.presentation.base.recycler.addItemDivider
import com.kivi.remote.presentation.base.recycler.initWithLinLay
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.databinding.DocumentsFragmentBinding
import com.redhotapp.driverapp.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DocumentsFragment : BaseFragment() {
    val TAG = "DocumentsFragment"
    private val docViewModel by viewModels<DocumentsViewModel>()
    private var adapter = DocumentsAdapter()
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

        docViewModel.documents.observe(viewLifecycleOwner, Observer { documetns ->

            adapter.swapData(documetns)
            Log.i(TAG, " got docs: $documetns ")

        })

        getDocuments()
        return view
    }

    private fun getDocuments() {
        docViewModel.getDocuments()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // recyclerview init
        docBinding.docsRecycler.initWithLinLay(LinearLayoutManager.VERTICAL, adapter, listOf())
        docBinding.docsRecycler.addItemDivider()

    }
}