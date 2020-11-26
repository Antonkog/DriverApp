package com.abona_erp.driverapp.ui.fdocuments

import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.databinding.LayoutEditOptionBinding
import com.abona_erp.driverapp.ui.utils.adapter.LazyAdapter

class EditOptionAdapter(listener: OnItemClickListener<EditOptionEntity>) :
    LazyAdapter<EditOptionEntity, LayoutEditOptionBinding>(listener) {
    override fun bindData(data: EditOptionEntity, binding: LayoutEditOptionBinding) {
        binding.root.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
        binding.option.text = data.type.title
        binding.option.setCompoundDrawablesRelativeWithIntrinsicBounds(0, data.icon, 0, 0)
    }

    override fun getLayoutId() = R.layout.layout_edit_option
}