package com.abona_erp.driverapp.ui.fprotocol

import androidx.core.content.res.ResourcesCompat
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.ChangeHistory
import com.abona_erp.driverapp.data.local.db.Status
import com.abona_erp.driverapp.databinding.RequestItemBinding
import com.kivi.remote.presentation.base.recycler.LazyAdapter


class ProtocolAdapter(listener: OnItemClickListener<ChangeHistory>) :
    LazyAdapter<ChangeHistory, RequestItemBinding>(listener) {

    override fun bindData(data: ChangeHistory, binding: RequestItemBinding) {

        binding.root.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
        binding.requestText.text = data.toString()

        when(data.status){
            Status.SENT-> binding.requestText.setTextColor(ResourcesCompat.getColor(binding.root.resources, R.color.confirm_gray, null))
            Status.SUCCESS-> binding.requestText.setTextColor(ResourcesCompat.getColor(binding.root.resources, R.color.confirm_green, null))
            Status.ERROR-> binding.requestText.setTextColor(ResourcesCompat.getColor(binding.root.resources, R.color.color_error, null))
        }
        binding.requestText.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
    }

    override fun getLayoutId(): Int = R.layout.request_item
}