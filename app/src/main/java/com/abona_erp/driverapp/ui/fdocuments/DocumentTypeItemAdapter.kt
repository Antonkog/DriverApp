package com.abona_erp.driverapp.ui.fdocuments

import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.databinding.DocumentTypeItemBinding
import com.abona_erp.driverapp.ui.utils.adapter.LazyAdapter

class DocumentTypeItemAdapter(
    itemClickListener: OnItemClickListener<DocumentTypeItem>
) : LazyAdapter<DocumentTypeItem, DocumentTypeItemBinding>(itemClickListener) {
    override fun bindData(data: DocumentTypeItem, binding: DocumentTypeItemBinding) {
        binding.documentItem.text = data.type
        binding.documentItem.isChecked = data.isChecked
        binding.documentItem.setOnClickListener {
            itemClickListener?.onLazyItemClick(data)
        }
    }

    override fun getLayoutId() = R.layout.document_type_item
}