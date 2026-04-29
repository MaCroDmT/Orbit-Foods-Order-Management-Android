package com.orbitfoods.ordersheet.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.orbitfoods.ordersheet.dashboard.DashboardActivity
import com.orbitfoods.ordersheet.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // If already logged in skip login screen
        if (auth.currentUser != null) {
            goToDashboard()
            return
        }

        // Login button
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "ইমেইল এবং পাসওয়ার্ড দিন", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show loading
            binding.btnLogin.isEnabled = false
            binding.btnLogin.text = "অপেক্ষা করুন..."

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(this, "স্বাগতম!", Toast.LENGTH_SHORT).show()
                    goToDashboard()
                }
                .addOnFailureListener {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "লগইন"
                    Toast.makeText(
                        this,
                        "লগইন ব্যর্থ। ইমেইল বা পাসওয়ার্ড ভুল।",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }

        // Forgot password
        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        // Register
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}