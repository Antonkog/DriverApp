package com.abona_erp.driverapp.ui.fdocuments.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.abona_erp.driverapp.R
import com.abona_erp.driverapp.ui.fdocuments.callback.CropBoundsChangeListener
import com.abona_erp.driverapp.ui.fdocuments.callback.OverlayViewChangeListener

@SuppressLint("CustomViewStyleable")
class UCropView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    var cropImageView: GestureCropImageView
        private set
    val overlayView: OverlayView
    private fun setListenersToViews() {
        cropImageView.cropBoundsChangeListener = object : CropBoundsChangeListener {
            override fun onCropAspectRatioChanged(cropRatio: Float) {
                overlayView.setTargetAspectRatio(cropRatio)
            }
        }
        overlayView.overlayViewChangeListener = object : OverlayViewChangeListener {
            override fun onCropRectUpdated(cropRect: RectF?) {
                cropImageView.setCropRect(cropRect!!)
            }
        }
    }

    override fun shouldDelayChildPressedState(): Boolean {
        return false
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.ucrop_view, this, true)
        cropImageView = findViewById(R.id.image_view_crop)
        overlayView = findViewById(R.id.view_overlay)
        val a = context.obtainStyledAttributes(attrs, R.styleable.ucrop_UCropView)
        overlayView.processStyledAttributes(a)
        cropImageView.processStyledAttributes(a)
        a.recycle()
        setListenersToViews()
    }
}