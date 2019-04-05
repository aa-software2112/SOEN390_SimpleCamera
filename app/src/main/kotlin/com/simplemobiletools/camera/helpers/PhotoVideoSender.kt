package com.simplemobiletools.camera.helpers

import android.content.Intent
import androidx.core.content.FileProvider
import com.simplemobiletools.camera.activities.MainActivity
import java.io.File

class PhotoVideoSender(val activity: MainActivity) {

    fun shareLastMedia(mediaPath : String, isPhoto : Boolean){
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = if (isPhoto) "image/jpeg"
                    else "video/mp4"
            val file = File(mediaPath)
            val uri = FileProvider.getUriForFile(activity.applicationContext,
                    activity.applicationContext.packageName + ".provider",
                    file)
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        val text = if (isPhoto) "Share picture to" else "Share video to"
        activity.startActivity(Intent.createChooser(shareIntent, text))
    }
}
