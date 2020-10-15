package com.abona_erp.driver.app.ui.fdocuments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.abona_erp.driver.app.R
import com.abona_erp.driver.app.data.local.db.DocumentEntity
import com.abona_erp.driver.app.databinding.DocumentsFragmentBinding
import com.abona_erp.driver.app.ui.base.BaseFragment
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.documents_fragment, container, false)

        docBinding = DocumentsFragmentBinding.bind(view).apply {
            viewmodel = docViewModel
        }

        docBinding.lifecycleOwner = this.viewLifecycleOwner

        docViewModel.error.observe(viewLifecycleOwner, Observer { error ->
            docBinding.textDocs.text = error.toString()
        })

        docViewModel.documents.observe(viewLifecycleOwner, Observer {
            if(it!= null && it.isNotEmpty()){
                adapter.swapData(it)
            } else Log.e(TAG, "got empty or null documents $it")
//            Log.e(TAG, "got tasks ${it}")
        })
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