package com.abona_erp.driver.app.ui.documents

import com.kivi.remote.presentation.base.recycler.LazyAdapter
import com.abona_erp.driver.app.R
import com.abona_erp.driver.app.data.model.DocumentResponse
import com.abona_erp.driver.app.databinding.DocumentItemBinding


class DocumentsAdapter : LazyAdapter<DocumentResponse, DocumentItemBinding>(null) {

    override fun bindData(data: DocumentResponse, binding: DocumentItemBinding) {
        binding.root.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
        binding.documentText.text = data.fileName
//        val ctx = binding.root.context
//
//        data.linkToFile?.let {
//            Glide.with(ctx)
//                .load(it)
//                .into(binding.documentImage)
//        }
    }

    override fun getLayoutId(): Int = R.layout.document_item
}