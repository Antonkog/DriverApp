package com.abona_erp.driverapp.ui.fdocuments.util

object CubicEasing {
    fun easeOut(
        time: Float,
        start: Float,
        end: Float,
        duration: Float
    ): Float {
        var currentTime = time
        return end * ((currentTime / duration - 1.0f.also { currentTime = it }) * currentTime * currentTime + 1.0f) + start
    }

    fun easeInOut(
        time: Float,
        start: Float,
        end: Float,
        duration: Float
    ): Float {
        var currentTime = time
        return if (duration / 2.0f.let { currentTime /= it; currentTime } < 1.0f) end / 2.0f * currentTime * currentTime * currentTime + start else end / 2.0f * (2.0f.let { currentTime -= it; currentTime } * currentTime * currentTime + 2.0f) + start
    }
}