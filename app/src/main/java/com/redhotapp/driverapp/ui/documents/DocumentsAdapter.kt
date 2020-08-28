package com.redhotapp.driverapp.ui.documents

import com.bumptech.glide.Glide
import com.kivi.remote.presentation.base.recycler.LazyAdapter
import com.redhotapp.driverapp.App
import com.redhotapp.driverapp.R
import com.redhotapp.driverapp.data.model.AppFileInterchangeItem
import com.redhotapp.driverapp.databinding.DocumentItemBinding


class DocumentsAdapter : LazyAdapter<AppFileInterchangeItem, DocumentItemBinding>(null) {

    override fun bindData(data: AppFileInterchangeItem, binding: DocumentItemBinding) {
        binding.root.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
//        binding.documentText.text = data.fileName
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