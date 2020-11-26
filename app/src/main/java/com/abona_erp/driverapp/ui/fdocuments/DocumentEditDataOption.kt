package com.abona_erp.driverapp.ui.fdocuments

import android.content.Context
import androidx.core.content.ContextCompat
import com.abona_erp.driverapp.R
import ja.burhanrashid52.photoeditor.PhotoFilter

object DocumentEditDataOption {

    fun getEditOption(): ArrayList<EditOptionEntity> {
        val editOptions = ArrayList<EditOptionEntity>()
        editOptions.add(EditOptionEntity(EditOptionEnum.BRUSH, R.drawable.ic_brush))
        editOptions.add(EditOptionEntity(EditOptionEnum.TEXT, R.drawable.ic_text))
        editOptions.add(EditOptionEntity(EditOptionEnum.CROP_ROTATE, R.drawable.ic_crop))
        editOptions.add(EditOptionEntity(EditOptionEnum.FILTER, R.drawable.ic_photo_filter))
        editOptions.add(EditOptionEntity(EditOptionEnum.REDO, R.drawable.ic_redo))
        editOptions.add(EditOptionEntity(EditOptionEnum.UNDO, R.drawable.ic_undo))
        return editOptions
    }

    fun getFilterOptions(): ArrayList<FilterOptionEntity> {
        val filterOptions = ArrayList<FilterOptionEntity>()
        filterOptions.add(FilterOptionEntity("filters/original.jpg", PhotoFilter.NONE))
        filterOptions.add(FilterOptionEntity("filters/auto_fix.png", PhotoFilter.AUTO_FIX))
        filterOptions.add(FilterOptionEntity("filters/brightness.png", PhotoFilter.BRIGHTNESS))
        filterOptions.add(FilterOptionEntity("filters/contrast.png", PhotoFilter.CONTRAST))
        filterOptions.add(FilterOptionEntity("filters/documentary.png", PhotoFilter.DOCUMENTARY))
        filterOptions.add(FilterOptionEntity("filters/dual_tone.png", PhotoFilter.DUE_TONE))
        filterOptions.add(FilterOptionEntity("filters/fill_light.png", PhotoFilter.FILL_LIGHT))
        filterOptions.add(FilterOptionEntity("filters/fish_eye.png", PhotoFilter.FISH_EYE))
        filterOptions.add(FilterOptionEntity("filters/grain.png", PhotoFilter.GRAIN))
        filterOptions.add(FilterOptionEntity("filters/gray_scale.png", PhotoFilter.GRAY_SCALE))
        filterOptions.add(FilterOptionEntity("filters/lomish.png", PhotoFilter.LOMISH))
        filterOptions.add(FilterOptionEntity("filters/negative.png", PhotoFilter.NEGATIVE))
        filterOptions.add(FilterOptionEntity("filters/posterize.png", PhotoFilter.POSTERIZE))
        filterOptions.add(FilterOptionEntity("filters/saturate.png", PhotoFilter.SATURATE))
        filterOptions.add(FilterOptionEntity("filters/sepia.png", PhotoFilter.SEPIA))
        filterOptions.add(FilterOptionEntity("filters/sharpen.png", PhotoFilter.SHARPEN))
        filterOptions.add(FilterOptionEntity("filters/temprature.png", PhotoFilter.TEMPERATURE))
        filterOptions.add(FilterOptionEntity("filters/tint.png", PhotoFilter.TINT))
        filterOptions.add(FilterOptionEntity("filters/vignette.png", PhotoFilter.VIGNETTE))
        filterOptions.add(
            FilterOptionEntity(
                "filters/cross_process.png",
                PhotoFilter.CROSS_PROCESS
            )
        )
        filterOptions.add(FilterOptionEntity("filters/b_n_w.png", PhotoFilter.BLACK_WHITE))
        filterOptions.add(
            FilterOptionEntity(
                "filters/flip_horizental.png",
                PhotoFilter.FLIP_HORIZONTAL
            )
        )
        filterOptions.add(
            FilterOptionEntity(
                "filters/flip_vertical.png",
                PhotoFilter.FLIP_VERTICAL
            )
        )
        return filterOptions
    }

    fun getDefaultColors(context: Context): List<Int> {
        val colorPickerColors = java.util.ArrayList<Int>()
        colorPickerColors.add(ContextCompat.getColor(context, R.color.blue_color_picker))
        colorPickerColors.add(ContextCompat.getColor(context, R.color.brown_color_picker))
        colorPickerColors.add(ContextCompat.getColor(context, R.color.green_color_picker))
        colorPickerColors.add(ContextCompat.getColor(context, R.color.orange_color_picker))
        colorPickerColors.add(ContextCompat.getColor(context, R.color.red_color_picker))
        colorPickerColors.add(ContextCompat.getColor(context, R.color.black))
        colorPickerColors.add(
            ContextCompat.getColor(
                context,
                R.color.red_orange_color_picker
            )
        )
        colorPickerColors.add(
            ContextCompat.getColor(
                context,
                R.color.sky_blue_color_picker
            )
        )
        colorPickerColors.add(ContextCompat.getColor(context, R.color.violet_color_picker))
        colorPickerColors.add(ContextCompat.getColor(context, R.color.white))
        colorPickerColors.add(ContextCompat.getColor(context, R.color.yellow_color_picker))
        colorPickerColors.add(
            ContextCompat.getColor(
                context,
                R.color.yellow_green_color_picker
            )
        )
        return colorPickerColors
    }
}