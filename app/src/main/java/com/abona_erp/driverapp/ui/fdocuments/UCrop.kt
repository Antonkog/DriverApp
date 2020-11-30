package com.abona_erp.driverapp.ui.fdocuments

import android.content.Intent
import android.net.Uri
import com.abona_erp.driverapp.BuildConfig

object UCrop {
    const val REQUEST_CROP = 69
    const val RESULT_ERROR = 96
    private const val EXTRA_PREFIX = BuildConfig.APPLICATION_ID
    const val EXTRA_OUTPUT_URI = "$EXTRA_PREFIX.OutputUri"
    const val EXTRA_OUTPUT_CROP_ASPECT_RATIO = "$EXTRA_PREFIX.CropAspectRatio"
    const val EXTRA_OUTPUT_IMAGE_WIDTH = "$EXTRA_PREFIX.ImageWidth"
    const val EXTRA_OUTPUT_IMAGE_HEIGHT = "$EXTRA_PREFIX.ImageHeight"
    const val EXTRA_OUTPUT_OFFSET_X = "$EXTRA_PREFIX.OffsetX"
    const val EXTRA_OUTPUT_OFFSET_Y = "$EXTRA_PREFIX.OffsetY"
    const val EXTRA_ERROR = "$EXTRA_PREFIX.Error"

    /**
     * Retrieve cropped image Uri from the result Intent
     *
     * @param intent crop result intent
     */
    fun getOutput(intent: Intent): Uri? {
        return intent.getParcelableExtra(EXTRA_OUTPUT_URI)
    }
}