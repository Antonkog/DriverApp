package com.abona_erp.driverapp.ui.fdocuments

import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.data.local.db.DocumentEntity
import com.abona_erp.driverapp.databinding.DocumentItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.abona_erp.driverapp.ui.utils.adapter.LazyAdapter


class DocumentsAdapter(listener: OnItemClickListener<DocumentEntity>) :
    LazyAdapter<DocumentEntity, DocumentItemBinding>(listener) {

    override fun bindData(data: DocumentEntity, binding: DocumentItemBinding) {
        binding.root.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
        Glide.with(binding.root.context).load(data.linkToFile)
            .diskCacheStrategy(DiskCacheStrategy.NONE).into(binding.documentImage)
        binding.documentText.text = data.fileName
        binding.documentContainer.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
    }

    override fun getLayoutId(): Int = R.layout.document_item
}