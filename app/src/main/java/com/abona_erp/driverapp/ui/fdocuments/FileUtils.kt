package com.abona_erp.driverapp.ui.fdocuments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {
    var FILE_NAME_DIVIDER = "_"

    @Throws(IOException::class)
    fun saveImage(
        context: Context,
        bmp: Bitmap,
        orderNo: String,
        taskId: String,
        mandantId: String
    ): File? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bytes = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val fileName = getFileName(orderNo, taskId, mandantId)
        val f = createImageFile(context, fileName)
        val fos = FileOutputStream(f)
        fos.write(bytes.toByteArray())
        fos.close()
        return f
    }

    fun getFileName(orderNo: String, taskId: String, mandantId: String): String {
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        return timeStamp + FILE_NAME_DIVIDER + mandantId + FILE_NAME_DIVIDER + orderNo + FILE_NAME_DIVIDER + taskId + FILE_NAME_DIVIDER
    }

    @Throws(IOException::class)
    fun createImageFile(
        context: Context,
        fileName: String
    ): File {
        val storageDir =
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        return File.createTempFile(fileName, ".png", storageDir)
    }
}