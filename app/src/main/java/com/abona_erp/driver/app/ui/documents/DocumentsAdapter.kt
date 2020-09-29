package com.abona_erp.driver.app.ui.documents

import com.kivi.remote.presentation.base.recycler.LazyAdapter
import com.abona_erp.driver.app.R
import com.abona_erp.driver.app.data.model.DocumentResponse
import com.abona_erp.driver.app.databinding.DocumentItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


class DocumentsAdapter : LazyAdapter<DocumentResponse, DocumentItemBinding>(null) {

    override fun bindData(data: DocumentResponse, binding: DocumentItemBinding) {
        binding.root.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
        Glide.with(binding.root.context).load(data.linkToFile).diskCacheStrategy(DiskCacheStrategy.NONE).into(binding.documentImage)
        binding.documentText.text = data.fileName
    }

    override fun getLayoutId(): Int = R.layout.document_item
}