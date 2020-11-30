package com.abona_erp.driverapp.ui.fdocuments.model

import android.graphics.RectF

data class ImageState(
    val cropRect: RectF,
    val currentImageRect: RectF,
    val currentScale: Float,
    val currentAngle: Float
) 