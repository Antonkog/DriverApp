package com.abona_erp.driverapp.ui.fprotocol

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.ChangeHistory
import com.abona_erp.driverapp.databinding.ProtocolFragmentBinding
import com.kivi.remote.presentation.base.recycler.LazyAdapter
import com.kivi.remote.presentation.base.recycler.initWithLinLay
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProtocolFragment : Fragment(), LazyAdapter.OnItemClickListener<ChangeHistory> {

    private val protocolViewModel by viewModels<ProtocolViewModel>()

    private lateinit var protocolFragmentBinding: ProtocolFragmentBinding


    private var adapter = ProtocolAdapter(this)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        protocolFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.protocol_fragment, container,
            false
        )

        protocolFragmentBinding.viewmodel = protocolViewModel
        protocolFragmentBinding.lifecycleOwner = this.viewLifecycleOwner

        protocolViewModel.requests.observe(viewLifecycleOwner, Observer {
            adapter.swapData(it)
        })

        return protocolFragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // recyclerview init
        protocolFragmentBinding.recyclerView.initWithLinLay(
            LinearLayoutManager.VERTICAL,
            adapter,
            listOf()
        )
    }

    override fun onLazyItemClick(data: ChangeHistory) {

    }

    companion object {
        const val TAG = "ProtocolFragment"
    }

}