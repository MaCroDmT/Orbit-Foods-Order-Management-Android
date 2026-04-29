package com.orbitfoods.ordersheet.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.orbitfoods.ordersheet.auth.LoginActivity
import com.orbitfoods.ordersheet.databinding.ActivityDashboardBinding
import com.orbitfoods.ordersheet.orders.OrderListActivity
import com.orbitfoods.ordersheet.products.ProductListActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "ড্যাশবোর্ড"

        // Load and show user name
        loadUserName()

        // Products button
        binding.btnProducts.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }

        // Orders button
        binding.btnOrders.setOnClickListener {
            startActivity(Intent(this, OrderListActivity::class.java))
        }

        // Logout button
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadUserName() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("name") ?: "ব্যবহারকারী"
                binding.tvWelcome.text = "স্বাগতম, $name!"
            }
    }
}