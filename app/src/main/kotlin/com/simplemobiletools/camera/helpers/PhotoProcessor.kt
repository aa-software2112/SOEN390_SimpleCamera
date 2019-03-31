package com.simplemobiletools.camera.helpers

import android.media.ExifInterface
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import com.simplemobiletools.camera.R
import com.simplemobiletools.camera.activities.MainActivity
import com.simplemobiletools.camera.extensions.config
import com.simplemobiletools.camera.extensions.getOutputMediaFile
import com.simplemobiletools.commons.extensions.* // ktlint-disable no-wildcard-imports
import com.simplemobiletools.commons.helpers.isNougatPlus
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.BitmapFactory
import android.graphics.Matrix
import com.simplemobiletools.camera.implementations.QRScanner

class PhotoProcessor(
    val activity: MainActivity,
    val saveUri: Uri?,
    val deviceOrientation: Int,
    val previewRotation: Int,
    val isUsingFrontCamera: Boolean,
    val isThirdPartyIntent: Boolean
) :
        AsyncTask<ByteArray, Void, String>() {

    override fun doInBackground(vararg params: ByteArray): String {
        var fos: OutputStream? = null
        val path: String
        try {
            path = if (saveUri != null) {
                saveUri.path
            } else {
                activity.getOutputMediaFile(true)
            }

            if (path.isEmpty()) {
                return ""
            }

            val data = params[0]
            val tempFile = File.createTempFile("simple_temp_exif", "")
            val tempFOS = FileOutputStream(tempFile)
            tempFOS.use {
                tempFOS.write(data)
            }
            val tempExif = ExifInterface(tempFile.absolutePath)

            val photoFile = File(path)
            if (activity.needsStupidWritePermissions(path)) {
                if (!activity.hasProperStoredTreeUri()) {
                    activity.toast(R.string.save_error_internal_storage)
                    activity.config.savePhotosFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()
                    return ""
                }

                var document = activity.getDocumentFile(path.getParentPath())
                document = document?.createFile("", path.substring(path.lastIndexOf('/') + 1))
                if (document == null) {
                    activity.toast(R.string.save_error_internal_storage)
                    return ""
                }

                fos = activity.contentResolver.openOutputStream(document.uri)
            } else {
                fos = if (saveUri == null) {
                    FileOutputStream(photoFile)
                } else {
                    activity.contentResolver.openOutputStream(saveUri)
                }
            }

            val exif = try {
                ExifInterface(path)
            } catch (e: Exception) {
                null
            }

            val orient = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
                    ?: ExifInterface.ORIENTATION_UNDEFINED

            val imageRot = orient.degreesFromOrientation()

            val deviceRot = compensateDeviceRotation(deviceOrientation, isUsingFrontCamera)
            var image = BitmapFactory.decodeByteArray(data, 0, data.size)
            val totalRotation = (imageRot + deviceRot + previewRotation) % 360

            if (path.startsWith(activity.internalStoragePath) || isNougatPlus() && !isThirdPartyIntent) {
                // do not rotate the image itself in these cases, rotate it by exif only
            } else {
                // make sure the image itself is rotated at third party intents
                image = rotate(image, totalRotation)
            }

            if (activity.addressFirstLine != "" && activity.addressSecondLine != "" && activity.addressCoordinates != "") {
                image = addLocationStamp(image, activity.addressFirstLine, activity.addressSecondLine, activity.addressCoordinates)
            }

            if (isUsingFrontCamera && activity.config.flipPhotos) {
                val matrix = Matrix()
                if (path.startsWith(activity.internalStoragePath)) {
                    matrix.preScale(1f, -1f)
                } else {
                    matrix.preScale(-1f, 1f)
                }

                try {
                    image = Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, false)
                } catch (e: OutOfMemoryError) {
                    activity.toast(R.string.out_of_memory_error)
                }
            }


            if (QRScanner.qr_in_progress || QRScanner.qr_requested)
            {
                QRScanner.getInstance().addQrPhoto(rotate(image, totalRotation));
                QRScanner.extractURL();
                QRScanner.qr_requested = false;
                return "";
            }

            try {
                image.compress(Bitmap.CompressFormat.JPEG, activity.config.photoQuality, fos)


                if (!isThirdPartyIntent) {
                    activity.saveImageRotation(path, totalRotation)
                }
            } catch (e: Exception) {
                activity.showErrorToast(e)
                return ""
            }



            if (activity.config.savePhotoMetadata && !isThirdPartyIntent) {
                val fileExif = ExifInterface(path)
                tempExif.copyTo(fileExif)
            }

            return photoFile.absolutePath
        } catch (e: FileNotFoundException) {
        } finally {
            fos?.close()
        }

        return ""
    }

    private fun rotate(bitmap: Bitmap, degree: Int): Bitmap? {
        if (degree == 0) {
            return bitmap
        }

        val width = bitmap.width
        val height = bitmap.height

        val matrix = Matrix()
        matrix.setRotate(degree.toFloat())

        try {
            return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        } catch (e: OutOfMemoryError) {
            activity.showErrorToast(e.toString())
        }
        return null
    }

    internal fun addLocationStamp(bitmap: Bitmap, addressFirstLine: String?, addressSecondLine: String?, addressCoordinates: String?): Bitmap? {

        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint()
        paint.setColor(Color.WHITE)
        paint.setTextSize(90F)

        val width = mutableBitmap.getScaledWidth(canvas) - mutableBitmap.getScaledWidth(canvas) + 50F
        val floatWidth = width.toFloat()

        canvas.drawText(addressFirstLine, floatWidth, 150F, paint)
        canvas.drawText(addressSecondLine, floatWidth, 250F, paint)

        paint.setTextSize(60F)
        canvas.drawText(addressCoordinates, floatWidth, 350F, paint)

        return mutableBitmap
    }

    override fun onPostExecute(path: String) {
        super.onPostExecute(path)
        if (path.isNotEmpty()) {
            activity.mediaSaved(path)
        }
    }

    interface MediaSavedListener {
        fun mediaSaved(path: String)
    }
}
