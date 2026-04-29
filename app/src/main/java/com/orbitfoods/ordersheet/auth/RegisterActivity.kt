package com.orbitfoods.ordersheet.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbitfoods.ordersheet.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        title = "নতুন অ্যাকাউন্ট"

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            // Validation
            if (name.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "সব তথ্য পূরণ করুন", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(
                    this,
                    "পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "পাসওয়ার্ড মিলছে না", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable button while loading
            binding.btnRegister.isEnabled = false
            binding.btnRegister.text = "অপেক্ষা করুন..."

            // Create account in Firebase Auth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val userId = result.user?.uid ?: ""

                    // Send verification email
                    result.user?.sendEmailVerification()

                    // Save user info to Firestore
                    val user = hashMapOf(
                        "userId" to userId,
                        "name" to name,
                        "email" to email
                    )

                    db.collection("users")
                        .document(userId)
                        .set(user)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "অ্যাকাউন্ট তৈরি হয়েছে! ইমেইল যাচাই করুন।",
                                Toast.LENGTH_LONG
                            ).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                }
                .addOnFailureListener {
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = "রেজিস্ট্রেশন করুন"
                    Toast.makeText(this, "ব্যর্থ: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }

        binding.tvLogin.setOnClickListener { finish() }
    }
}