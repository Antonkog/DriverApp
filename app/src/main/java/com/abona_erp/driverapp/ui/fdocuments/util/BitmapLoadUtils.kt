package com.abona_erp.driverapp.ui.fdocuments.util

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.view.Display
import android.view.WindowManager
import androidx.exifinterface.media.ExifInterface
import com.abona_erp.driverapp.ui.fdocuments.callback.BitmapLoadCallback
import com.abona_erp.driverapp.ui.fdocuments.task.BitmapLoadTask
import java.io.Closeable
import java.io.IOException
import kotlin.math.pow
import kotlin.math.sqrt

object BitmapLoadUtils {
    private const val TAG = "BitmapLoadUtils"

    fun decodeBitmapInBackground(
        context: Context,
        uri: Uri, outputUri: Uri?,
        requiredWidth: Int, requiredHeight: Int,
        loadCallback: BitmapLoadCallback?
    ) {
        BitmapLoadTask(context, uri, outputUri, requiredWidth, requiredHeight, loadCallback!!)
            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    fun transformBitmap(bitmap: Bitmap, transformMatrix: Matrix): Bitmap {
        var currentBitmap = bitmap
        try {
            val converted = Bitmap.createBitmap(
                currentBitmap,
                0,
                0,
                currentBitmap.width,
                currentBitmap.height,
                transformMatrix,
                true
            )
            if (!currentBitmap.sameAs(converted)) {
                currentBitmap = converted
            }
        } catch (error: OutOfMemoryError) {
            Log.e(TAG, "transformBitmap: ", error)
        }
        return currentBitmap
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width lower or equal to the requested height and width.
            while (height / inSampleSize > reqHeight || width / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    fun getExifOrientation(context: Context, imageUri: Uri): Int {
        var orientation = ExifInterface.ORIENTATION_UNDEFINED
        try {
            val stream =
                context.contentResolver.openInputStream(imageUri)
                    ?: return orientation
            orientation = ImageHeaderParser(stream)
                .orientation
            close(stream)
        } catch (e: IOException) {
            Log.e(TAG, "getExifOrientation: $imageUri", e)
        }
        return orientation
    }

    fun exifToDegrees(exifOrientation: Int): Int {
        return when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90, ExifInterface.ORIENTATION_TRANSPOSE -> 90
            ExifInterface.ORIENTATION_ROTATE_180, ExifInterface.ORIENTATION_FLIP_VERTICAL -> 180
            ExifInterface.ORIENTATION_ROTATE_270, ExifInterface.ORIENTATION_TRANSVERSE -> 270
            else -> 0
        }
    }

    fun exifToTranslation(exifOrientation: Int): Int {
        return when (exifOrientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL, ExifInterface.ORIENTATION_FLIP_VERTICAL, ExifInterface.ORIENTATION_TRANSPOSE, ExifInterface.ORIENTATION_TRANSVERSE -> -1
            else -> 1
        }
    }

    /**
     * This method calculates maximum size of both width and height of bitmap.
     * It is twice the device screen diagonal for default implementation (extra quality to zoom image).
     * Size cannot exceed max texture size.
     *
     * @return - max bitmap size in pixels.
     */
    fun calculateMaxBitmapSize(context: Context): Int {
        val wm =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display
        val width: Int
        val height: Int
        val size = Point()
        display = wm.defaultDisplay
        display.getSize(size)
        width = size.x
        height = size.y

        // Twice the device screen diagonal as default
        var maxBitmapSize = sqrt(
            width.toDouble().pow(2.0) + height.toDouble().pow(2.0)
        ).toInt()

        // Check for max texture size via Canvas
        val canvas = Canvas()
        val maxCanvasSize =
            canvas.maximumBitmapWidth.coerceAtMost(canvas.maximumBitmapHeight)
        if (maxCanvasSize > 0) {
            maxBitmapSize = maxBitmapSize.coerceAtMost(maxCanvasSize)
        }

        // Check for max texture size via GL
        val maxTextureSize = EglUtils.maxTextureSize
        if (maxTextureSize > 0) {
            maxBitmapSize = maxBitmapSize.coerceAtMost(maxTextureSize)
        }
        Log.d(TAG, "maxBitmapSize: $maxBitmapSize")
        return maxBitmapSize
    }

    fun close(c: Closeable?) {
        if (c != null) { // java.lang.IncompatibleClassChangeError: interface not implemented
            try {
                c.close()
            } catch (e: IOException) {
                // silence
            }
        }
    }
}