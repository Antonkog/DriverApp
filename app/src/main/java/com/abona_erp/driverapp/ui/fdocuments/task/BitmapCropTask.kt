package com.abona_erp.driverapp.ui.fdocuments.task

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Matrix
import android.graphics.RectF
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.abona_erp.driverapp.ui.fdocuments.callback.BitmapCropCallback
import com.abona_erp.driverapp.ui.fdocuments.model.CropParameters
import com.abona_erp.driverapp.ui.fdocuments.model.ExifInfo
import com.abona_erp.driverapp.ui.fdocuments.model.ImageState
import com.abona_erp.driverapp.ui.fdocuments.util.BitmapLoadUtils
import com.abona_erp.driverapp.ui.fdocuments.util.FileUtils
import com.abona_erp.driverapp.ui.fdocuments.util.ImageHeaderParser
import java.io.*
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Crops part of image that fills the crop bounds.
 *
 *
 * First image is downscaled if max size was set and if resulting image is larger that max size.
 * Then image is rotated accordingly.
 * Finally new Bitmap object is created and saved to file.
 */
open class BitmapCropTask(
    viewBitmap: Bitmap?,
    imageState: ImageState,
    cropParameters: CropParameters,
    cropCallback: BitmapCropCallback?
) : AsyncTask<Void?, Void?, Throwable?>() {
    private var mViewBitmap: Bitmap?
    private val mCropRect: RectF
    private val mCurrentImageRect: RectF
    private var mCurrentScale: Float
    private val mCurrentAngle: Float
    private val mMaxResultImageSizeX: Int
    private val mMaxResultImageSizeY: Int
    private val mCompressFormat: CompressFormat
    private val mCompressQuality: Int
    private val mImageInputPath: String
    private val mImageOutputPath: String
    private val mExifInfo: ExifInfo
    private val mCropCallback: BitmapCropCallback?
    private var mCroppedImageWidth = 0
    private var mCroppedImageHeight = 0
    private var cropOffsetX = 0
    private var cropOffsetY = 0

    override fun doInBackground(vararg p0: Void?): Throwable? {
        when {
            mViewBitmap == null -> {
                return NullPointerException("ViewBitmap is null")
            }
            mViewBitmap!!.isRecycled -> {
                return NullPointerException("ViewBitmap is recycled")
            }
            mCurrentImageRect.isEmpty -> {
                return NullPointerException("CurrentImageRect is empty")
            }
            else -> {
                mViewBitmap = try {
                    crop()
                    null
                } catch (throwable: Throwable) {
                    return throwable
                }
                return null
            }
        }
    }

    @Throws(IOException::class)
    private fun crop(): Boolean {
        // Downsize if needed
        if (mMaxResultImageSizeX > 0 && mMaxResultImageSizeY > 0) {
            val cropWidth = mCropRect.width() / mCurrentScale
            val cropHeight = mCropRect.height() / mCurrentScale
            if (cropWidth > mMaxResultImageSizeX || cropHeight > mMaxResultImageSizeY) {
                val scaleX = mMaxResultImageSizeX / cropWidth
                val scaleY = mMaxResultImageSizeY / cropHeight
                val resizeScale = scaleX.coerceAtMost(scaleY)
                val resizedBitmap = Bitmap.createScaledBitmap(
                    mViewBitmap!!,
                    (mViewBitmap!!.width * resizeScale).roundToInt(),
                    (mViewBitmap!!.height * resizeScale).roundToInt(), false
                )
                if (mViewBitmap != resizedBitmap) {
                    mViewBitmap!!.recycle()
                }
                mViewBitmap = resizedBitmap
                mCurrentScale /= resizeScale
            }
        }

        // Rotate if needed
        if (mCurrentAngle != 0f) {
            val tempMatrix = Matrix()
            tempMatrix.setRotate(
                mCurrentAngle,
                mViewBitmap!!.width / 2.toFloat(),
                mViewBitmap!!.height / 2.toFloat()
            )
            val rotatedBitmap = Bitmap.createBitmap(
                mViewBitmap!!, 0, 0, mViewBitmap!!.width, mViewBitmap!!.height,
                tempMatrix, true
            )
            if (mViewBitmap != rotatedBitmap) {
                mViewBitmap!!.recycle()
            }
            mViewBitmap = rotatedBitmap
        }
        cropOffsetX =
            ((mCropRect.left - mCurrentImageRect.left) / mCurrentScale).roundToInt()
        cropOffsetY = ((mCropRect.top - mCurrentImageRect.top) / mCurrentScale).roundToInt()
        mCroppedImageWidth = (mCropRect.width() / mCurrentScale).roundToInt()
        mCroppedImageHeight = (mCropRect.height() / mCurrentScale).roundToInt()
        val shouldCrop = shouldCrop(mCroppedImageWidth, mCroppedImageHeight)
        Log.i(TAG, "Should crop: $shouldCrop")
        return if (shouldCrop) {
            val originalExif =
                ExifInterface(mImageInputPath)
            saveImage(
                Bitmap.createBitmap(
                    mViewBitmap!!,
                    cropOffsetX,
                    cropOffsetY,
                    mCroppedImageWidth,
                    mCroppedImageHeight
                )
            )
            if (mCompressFormat == CompressFormat.JPEG) {
                ImageHeaderParser.copyExif(
                    originalExif,
                    mCroppedImageWidth,
                    mCroppedImageHeight,
                    mImageOutputPath
                )
            }
            true
        } else {
            FileUtils.copyFile(
                mImageInputPath,
                mImageOutputPath
            )
            false
        }
    }

    @Throws(FileNotFoundException::class)
    private fun saveImage(croppedBitmap: Bitmap) {
        var outputStream: OutputStream? = null
        var outStream: ByteArrayOutputStream? = null
        try {
            outputStream = FileOutputStream(File(mImageOutputPath), false)
            outStream = ByteArrayOutputStream()
            croppedBitmap.compress(mCompressFormat, mCompressQuality, outStream)
            outputStream.write(outStream.toByteArray())
            croppedBitmap.recycle()
        } catch (exc: IOException) {
            Log.e(TAG, exc.localizedMessage ?: "")
        } finally {
            BitmapLoadUtils.close(outputStream)
            BitmapLoadUtils.close(outStream)
        }
    }

    /**
     * Check whether an image should be cropped at all or just file can be copied to the destination path.
     * For each 1000 pixels there is one pixel of error due to matrix calculations etc.
     *
     * @param width  - crop area width
     * @param height - crop area height
     * @return - true if image must be cropped, false - if original image fits requirements
     */
    private fun shouldCrop(width: Int, height: Int): Boolean {
        var pixelError = 1
        pixelError += (width.coerceAtLeast(height) / 1000f).roundToInt()
        return (mMaxResultImageSizeX > 0 && mMaxResultImageSizeY > 0
                || abs(mCropRect.left - mCurrentImageRect.left) > pixelError || abs(
            mCropRect.top - mCurrentImageRect.top
        ) > pixelError || abs(mCropRect.bottom - mCurrentImageRect.bottom) > pixelError || abs(
            mCropRect.right - mCurrentImageRect.right
        ) > pixelError || mCurrentAngle != 0f)
    }

    override fun onPostExecute(t: Throwable?) {
        if (mCropCallback != null) {
            if (t == null) {
                val uri = Uri.fromFile(File(mImageOutputPath))
                mCropCallback.onBitmapCropped(
                    uri,
                    cropOffsetX,
                    cropOffsetY,
                    mCroppedImageWidth,
                    mCroppedImageHeight
                )
            } else {
                mCropCallback.onCropFailure(t)
            }
        }
    }

    companion object {
        private const val TAG = "BitmapCropTask"
    }

    init {
        mViewBitmap = viewBitmap
        mCropRect = imageState.cropRect
        mCurrentImageRect = imageState.currentImageRect
        mCurrentScale = imageState.currentScale
        mCurrentAngle = imageState.currentAngle
        mMaxResultImageSizeX = cropParameters.maxResultImageSizeX
        mMaxResultImageSizeY = cropParameters.maxResultImageSizeY
        mCompressFormat = cropParameters.compressFormat
        mCompressQuality = cropParameters.compressQuality
        mImageInputPath = cropParameters.imageInputPath
        mImageOutputPath = cropParameters.imageOutputPath
        mExifInfo = cropParameters.exifInfo
        mCropCallback = cropCallback
    }
}