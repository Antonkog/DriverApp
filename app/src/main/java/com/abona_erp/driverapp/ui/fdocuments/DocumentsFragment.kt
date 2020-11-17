package com.abona_erp.driverapp.ui.fdocuments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.DocumentEntity
import com.abona_erp.driverapp.databinding.DocumentsFragmentBinding
import com.abona_erp.driverapp.ui.base.BaseFragment
import com.abona_erp.driverapp.ui.utils.DeviceUtils
import com.kivi.remote.presentation.base.recycler.LazyAdapter
import com.kivi.remote.presentation.base.recycler.addItemDivider
import com.kivi.remote.presentation.base.recycler.initWithLinLay
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


        val taskId: Int? = args.taskEntity?.taskId
        val mandant: Int? = args.taskEntity?.mandantId

        taskId?.let {
            docViewModel.observeDocuments(it).observe(viewLifecycleOwner, { documents ->
                if (documents != null && documents.isNotEmpty()) {
                    adapter.swapData(documents)
                } else Log.e(TAG, "got empty or null documents $it")
            })
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.action_send_doc).let {
            it?.setVisible(true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // recyclerview init
        docBinding.docsRecycler.initWithLinLay(LinearLayoutManager.VERTICAL, adapter, listOf())
        docBinding.docsRecycler.addItemDivider()

    }

    override fun onLazyItemClick(data: DocumentEntity) {
        Log.e(TAG, "got document click $data")
    }
}