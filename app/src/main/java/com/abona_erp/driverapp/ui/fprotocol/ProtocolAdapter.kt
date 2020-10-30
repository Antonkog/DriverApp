package com.abona_erp.driverapp.ui.fprotocol

import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.RequestEntity
import com.abona_erp.driverapp.databinding.RequestItemBinding
import com.kivi.remote.presentation.base.recycler.LazyAdapter


class ProtocolAdapter(listener: OnItemClickListener<RequestEntity>) :
    LazyAdapter<RequestEntity, RequestItemBinding>(listener) {

    override fun bindData(data: RequestEntity, binding: RequestItemBinding) {

        binding.root.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
        binding.requestText.text = data.requestJsonBody
        binding.requestText.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
    }

    override fun getLayoutId(): Int = R.layout.request_item
}