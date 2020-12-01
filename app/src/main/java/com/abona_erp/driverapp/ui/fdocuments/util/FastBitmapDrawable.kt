/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.abona_erp.driverapp.ui.fdocuments.util

import android.graphics.*
import android.graphics.drawable.Drawable

class FastBitmapDrawable(var currentBitmap: Bitmap?) : Drawable() {
    private val mPaint =
        Paint(Paint.FILTER_BITMAP_FLAG)
    private var mAlpha = 255
    private var mWidth = 0
    private var mHeight = 0

    init {
        if (currentBitmap != null) {
            mWidth = currentBitmap!!.width
            mHeight = currentBitmap!!.height
        } else {
            mHeight = 0
            mWidth = mHeight
        }
    }

    override fun draw(canvas: Canvas) {
        if (currentBitmap != null && !currentBitmap!!.isRecycled) {
            canvas.drawBitmap(currentBitmap!!, null, bounds, mPaint)
        }
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setFilterBitmap(filterBitmap: Boolean) {
        mPaint.isFilterBitmap = filterBitmap
    }

    override fun getAlpha(): Int {
        return mAlpha
    }

    override fun setAlpha(alpha: Int) {
        mAlpha = alpha
        mPaint.alpha = alpha
    }

    override fun getIntrinsicWidth(): Int {
        return mWidth
    }

    override fun getIntrinsicHeight(): Int {
        return mHeight
    }

    override fun getMinimumWidth(): Int {
        return mWidth
    }

    override fun getMinimumHeight(): Int {
        return mHeight
    }

    fun getBitmap(): Bitmap? {
        return currentBitmap
    }
}