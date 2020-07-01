package com.redhotapp.driverapp.document

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.redhotapp.driverapp.R

class DocumentFragment : Fragment() {

    companion object {
        fun newInstance() = DocumentFragment()
    }

    private lateinit var viewModel: DocumentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_document, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DocumentViewModel::class.java)
        // TODO: Use the ViewModel
    }

}