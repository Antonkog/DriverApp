package com.abona_erp.driverapp.ui.fdocuments.model

import android.graphics.Bitmap.CompressFormat

data class CropParameters(
    val maxResultImageSizeX: Int,
    val maxResultImageSizeY: Int,
    val compressFormat: CompressFormat,
    val compressQuality: Int,
    val imageInputPath: String,
    val imageOutputPath: String,
    val exifInfo: ExifInfo
) 