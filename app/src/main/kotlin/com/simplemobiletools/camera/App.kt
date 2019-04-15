package com.simplemobiletools.camera

import android.app.Application
import com.google.firebase.FirebaseApp
import com.simplemobiletools.commons.extensions.checkUseEnglish

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        checkUseEnglish()
    }
}
