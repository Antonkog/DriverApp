package com.abona_erp.driverapp.ui.fdocuments.util

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.os.Build


/**
 * Hack class to properly support state drawable back to Android 1.6
 */
class SelectedStateListDrawable(drawable: Drawable?, private val mSelectionColor: Int) :
    StateListDrawable() {
    override fun onStateChange(states: IntArray): Boolean {
        var isStatePressedInArray = false
        for (state in states) {
            if (state == android.R.attr.state_selected) {
                isStatePressedInArray = true
            }
        }
        if (isStatePressedInArray) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                super.setColorFilter(BlendModeColorFilter(mSelectionColor, BlendMode.SRC_ATOP))
            } else {
                @Suppress("DEPRECATION")
                super.setColorFilter(mSelectionColor, PorterDuff.Mode.SRC_ATOP)
            }
        } else {
            super.clearColorFilter()
        }
        return super.onStateChange(states)
    }

    override fun isStateful(): Boolean {
        return true
    }

    init {
        addState(intArrayOf(android.R.attr.state_selected), drawable)
        addState(intArrayOf(), drawable)
    }
}