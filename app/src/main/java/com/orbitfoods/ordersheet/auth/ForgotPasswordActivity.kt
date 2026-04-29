package com.orbitfoods.ordersheet.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.orbitfoods.ordersheet.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        title = "পাসওয়ার্ড রিসেট"

        binding.btnResetPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "ইমেইল দিন", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnResetPassword.isEnabled = false
            binding.btnResetPassword.text = "পাঠানো হচ্ছে..."

            // Firebase sends reset email automatically
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "পাসওয়ার্ড রিসেট লিংক পাঠানো হয়েছে!\nআপনার ইমেইল চেক করুন।",
                        Toast.LENGTH_LONG
                    ).show()
                    finish() // Go back to login
                }
                .addOnFailureListener {
                    binding.btnResetPassword.isEnabled = true
                    binding.btnResetPassword.text = "পাঠান"
                    Toast.makeText(
                        this,
                        "ব্যর্থ: ইমেইল সঠিক নয়",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}