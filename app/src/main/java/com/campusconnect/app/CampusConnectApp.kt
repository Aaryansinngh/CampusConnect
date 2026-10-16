package com.campusconnect.app

import android.app.Application
import com.google.firebase.FirebaseApp

class CampusConnectApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            // Firebase credentials missing or default app initialized safely; mock layer fallback handles offline mode.
        }
    }
}
