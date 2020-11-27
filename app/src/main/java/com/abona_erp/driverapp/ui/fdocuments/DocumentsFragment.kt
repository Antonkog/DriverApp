package com.abona_erp.driverapp.ui.fdocuments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.DocumentEntity
import com.abona_erp.driverapp.databinding.DocumentsFragmentBinding
import com.abona_erp.driverapp.ui.base.BaseFragment
import com.abona_erp.driverapp.ui.utils.DeviceUtils
import com.abona_erp.driverapp.ui.utils.adapter.LazyAdapter
import com.abona_erp.driverapp.ui.utils.adapter.addItemDivider
import com.abona_erp.driverapp.ui.utils.adapter.initWithLinLay
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DocumentsFragment : BaseFragment(), LazyAdapter.OnItemClickListener<DocumentEntity> {
    val TAG = "DocumentsFragment"
    private val docViewModel by viewModels<DocumentsViewModel>()
    private var adapter = DocumentsAdapter(this)
    private lateinit var docBinding: DocumentsFragmentBinding

    val args: DocumentsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.documents_fragment, container, false)

        docBinding = DocumentsFragmentBinding.bind(view).apply {
            viewmodel = docViewModel
        }

        docBinding.lifecycleOwner = this.viewLifecycleOwner


        val taskId: Int? = args.taskData.taskId
        val mandant: Int? = args.taskData.mandantId
        docBinding.uploadDocument.setOnClickListener {
            val bundle =
                bundleOf(
                    docBinding.root.context.getString(R.string.key_task_data) to args.taskData
                )
            findNavController().navigate(R.id.action_nav_document_to_selectDocumentFragment, bundle)
        }
        taskId?.let {
            docViewModel.observeDocuments(it).observe(viewLifecycleOwner) { documents ->
                if (documents.isNotEmpty()) {
                    docBinding.documentList.visibility = View.VISIBLE
                    docBinding.emptyDocumentView.visibility = View.GONE
                    adapter.swapData(documents)
                } else {
                    docBinding.documentList.visibility = View.GONE
                    docBinding.emptyDocumentView.visibility = View.VISIBLE
                    Log.e(TAG, "got empty or null documents $it")
                }
            }
        }

        if (taskId != null && mandant != null) {
            docViewModel.refreshDocuments(mandant, taskId, DeviceUtils.getUniqueID(context))
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // recyclerview init
        docBinding.documentList.initWithLinLay(LinearLayoutManager.VERTICAL, adapter, listOf())
        docBinding.documentList.addItemDivider()

    }

    override fun onLazyItemClick(data: DocumentEntity) {
        Log.e(TAG, "got document click $data")
    }
}