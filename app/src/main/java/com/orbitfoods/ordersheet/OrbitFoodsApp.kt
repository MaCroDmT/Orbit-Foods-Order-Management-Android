package com.orbitfoods.ordersheet

import android.app.Application
import com.google.firebase.FirebaseApp

class OrbitFoodsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}