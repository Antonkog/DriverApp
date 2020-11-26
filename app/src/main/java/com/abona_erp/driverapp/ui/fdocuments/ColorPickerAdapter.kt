package com.abona_erp.driverapp.ui.fdocuments

import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.databinding.ColorPickerItemListBinding
import com.abona_erp.driverapp.ui.base.BaseFragment
import com.abona_erp.driverapp.ui.utils.adapter.LazyAdapter

class ColorPickerAdapter(
    itemClickListener: OnItemClickListener<Int>
) : LazyAdapter<Int, ColorPickerItemListBinding>(itemClickListener) {

    override fun bindData(data: Int, binding: ColorPickerItemListBinding) {
        binding.root.setOnClickListener { itemClickListener?.onLazyItemClick(data) }
        binding.colorPickerView.setBackgroundColor(data)
    }

    override fun getLayoutId() = R.layout.color_picker_item_list
}
