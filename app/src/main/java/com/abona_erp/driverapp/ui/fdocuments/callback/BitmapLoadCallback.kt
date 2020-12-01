package com.abona_erp.driverapp.ui.fdocuments.callback

import android.graphics.Bitmap
import com.abona_erp.driverapp.ui.fdocuments.model.ExifInfo

interface BitmapLoadCallback {
    fun onBitmapLoaded(
        bitmap: Bitmap,
        exifInfo: ExifInfo,
        imageInputPath: String,
        imageOutputPath: String?
    )

    fun onFailure(bitmapWorkerException: Exception)
}