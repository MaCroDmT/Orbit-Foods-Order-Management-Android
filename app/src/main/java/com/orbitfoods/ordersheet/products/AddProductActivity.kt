package com.orbitfoods.ordersheet.products

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.orbitfoods.ordersheet.databinding.ActivityAddProductBinding

class AddProductActivity : AppCompatActivity() {

    // This connects our Kotlin code to the XML layout
    private lateinit var binding: ActivityAddProductBinding

    // This is our connection to Firebase database
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Connect to XML layout
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set screen title
        title = "নতুন পণ্য যোগ করুন"

        // When Save button is clicked
        binding.btnSave.setOnClickListener {

            // Get what the user typed
            val name = binding.etProductName.text.toString().trim()

            // Check if empty
            if (name.isEmpty()) {
                Toast.makeText(this, "পণ্যের নাম লিখুন", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Prepare data to save
            val product = hashMapOf(
                "productName" to name
            )

            // Save to Firebase Firestore
            db.collection("products")
                .add(product)
                .addOnSuccessListener {
                    // Success! Show message and go back
                    Toast.makeText(this, "পণ্য যোগ হয়েছে!", Toast.LENGTH_SHORT).show()
                    finish() // This closes this screen and goes back
                }
                .addOnFailureListener {
                    // Something went wrong
                    Toast.makeText(this, "ব্যর্থ: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
