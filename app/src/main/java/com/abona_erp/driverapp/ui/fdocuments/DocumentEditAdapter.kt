package com.abona_erp.driverapp.ui.fdocuments

import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.databinding.DocumentEditItemBinding
import com.abona_erp.driverapp.ui.utils.adapter.LazyAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class DocumentEditAdapter(
    listener: OnItemClickListener<UploadDocumentItem>,
    var removeListener: DocumentRemoveListener
) :
    LazyAdapter<UploadDocumentItem, DocumentEditItemBinding>(listener) {

    override fun bindData(data: UploadDocumentItem, binding: DocumentEditItemBinding) {
        binding.root.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
        Glide.with(binding.root.context).load(data.uri)
            .diskCacheStrategy(DiskCacheStrategy.NONE).into(binding.image)
        binding.remove.setOnClickListener { removeListener.onRemove(data) }
    }

    override fun getLayoutId() = R.layout.document_edit_item
}