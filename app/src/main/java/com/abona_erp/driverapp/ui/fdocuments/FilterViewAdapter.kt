package com.abona_erp.driverapp.ui.fdocuments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.databinding.LayoutFilterOptionBinding
import com.abona_erp.driverapp.ui.utils.adapter.LazyAdapter
import java.io.IOException
import java.io.InputStream

class FilterViewAdapter(var listener: OnItemClickListener<FilterOptionEntity>) :
    LazyAdapter<FilterOptionEntity, LayoutFilterOptionBinding>(listener) {
    override fun bindData(data: FilterOptionEntity, binding: LayoutFilterOptionBinding) {
        binding.root.setOnClickListener { listener.onLazyItemClick(data) }
        val fromAsset: Bitmap? = getBitmapFromAsset(binding.root.context, data.image)
        binding.imgFilterView.setImageBitmap(fromAsset)
        binding.txtFilterName.text = data.photoFilter.name.replace("_", " ")
    }

    private fun getBitmapFromAsset(
        context: Context,
        strName: String
    ): Bitmap? {
        val assetManager = context.assets
        val inputStream: InputStream?
        return try {
            inputStream = assetManager.open(strName)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override fun getLayoutId() = R.layout.layout_filter_option

}