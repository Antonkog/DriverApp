/*
 * Copyright (C) 2007-2008 OpenIntents.org
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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.*
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws

object FileUtils {
    /**
     * Copies one file into the other with the given paths.
     * In the event that the paths are the same, trying to copy one file to the other
     * will cause both files to become null.
     * Simply skipping this step if the paths are identical.
     */
    @Throws(IOException::class)
    fun copyFile(pathFrom: String, pathTo: String) {
        if (pathFrom.equals(pathTo, ignoreCase = true)) {
            return
        }
        var outputChannel: FileChannel? = null
        var inputChannel: FileChannel? = null
        try {
            inputChannel = FileInputStream(File(pathFrom)).channel
            outputChannel = FileOutputStream(File(pathTo)).channel
            inputChannel.transferTo(0, inputChannel.size(), outputChannel)
            inputChannel.close()
        } finally {
            inputChannel?.close()
            outputChannel?.close()
        }
    }

    var FILE_NAME_DIVIDER = "_"

    @Throws(IOException::class)
    fun saveImage(
        context: Context,
        bmp: Bitmap,
        orderNo: String,
        taskId: String,
        mandantId: String
    ): File {
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