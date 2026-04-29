package com.orbitfoods.ordersheet.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.orbitfoods.ordersheet.R
import com.orbitfoods.ordersheet.dashboard.DashboardActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Wait 2 seconds then decide where to go
        Handler(Looper.getMainLooper()).postDelayed({

            val auth = FirebaseAuth.getInstance()

            if (auth.currentUser != null) {
                // User already logged in → go to Dashboard
                startActivity(Intent(this, DashboardActivity::class.java))
            } else {
                // Not logged in → go to Login
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()

        }, 2000) // 2000 = 2 seconds
    }
}