package com.orbitfoods.ordersheet.orders

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.orbitfoods.ordersheet.databinding.ActivityOrderListBinding

class OrderListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderListBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: OrderAdapter
    private val orderList = mutableListOf<OrderSummary>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "অর্ডার তালিকা"

        setupRecyclerView()
        loadOrders()

        binding.btnNewOrder.setOnClickListener {
            startActivity(Intent(this, CreateOrderActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = OrderAdapter(
            orderList,
            onView = { order ->
                // Go to ViewOrderActivity
                val intent = Intent(this, ViewOrderActivity::class.java)
                intent.putExtra("orderId", order.orderId)
                startActivity(intent)
            },
            onDelete = { order ->
                deleteOrder(order)
            }
        )
        binding.recyclerOrders.layoutManager = LinearLayoutManager(this)
        binding.recyclerOrders.adapter = adapter
    }

    private fun loadOrders() {
        db.collection("orders")
            .orderBy("serialNumber")
            .get()
            .addOnSuccessListener { documents ->
                orderList.clear()

                for (doc in documents) {
                    val order = OrderSummary(
                        orderId = doc.id,
                        serialNumber = doc.getLong("serialNumber") ?: 0,
                        date = doc.getString("date") ?: "",
                        customerName = doc.getString("customerName") ?: "",
                        address = doc.getString("address") ?: "",
                        totalAmount = doc.getDouble("totalAmount") ?: 0.0
                    )
                    orderList.add(order)
                }

                adapter.updateList(orderList)

                // Update order count
                binding.tvOrderCount.text = "${orderList.size} টি অর্ডার"

                // Show empty message if no orders
                if (orderList.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.recyclerOrders.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.recyclerOrders.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "লোড ব্যর্থ: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteOrder(order: OrderSummary) {
        db.collection("orders")
            .document(order.orderId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "অর্ডার মুছে গেছে", Toast.LENGTH_SHORT).show()
                loadOrders() // Refresh list
            }
            .addOnFailureListener {
                Toast.makeText(this, "মুছতে ব্যর্থ: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Refresh when coming back from another screen
    override fun onResume() {
        super.onResume()
        loadOrders()
    }
}
