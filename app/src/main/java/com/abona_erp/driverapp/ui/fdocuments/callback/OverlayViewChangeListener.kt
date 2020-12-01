package com.abona_erp.driverapp.ui.fdocuments.callback

import android.graphics.RectF

interface OverlayViewChangeListener {
    fun onCropRectUpdated(cropRect: RectF?)
}