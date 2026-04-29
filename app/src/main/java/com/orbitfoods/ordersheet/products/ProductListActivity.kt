package com.orbitfoods.ordersheet.products

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.orbitfoods.ordersheet.databinding.ActivityProductListBinding
import com.orbitfoods.ordersheet.models.Product

class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: ProductAdapter
    private val productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "পণ্য তালিকা"

        // Set up the RecyclerView
        setupRecyclerView()

        // Load products from Firebase
        loadProducts()

        // Add new product button
        binding.btnAddProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(
            productList,
            onEdit = { product -> editProduct(product) },
            onDelete = { product -> deleteProduct(product) }
        )
        binding.recyclerProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerProducts.adapter = adapter
    }

    private fun loadProducts() {
        db.collection("products")
            .get()
            .addOnSuccessListener { documents ->
                productList.clear()
                for (doc in documents) {
                    val product = Product(
                        productId = doc.id,
                        productName = doc.getString("productName") ?: ""
                    )
                    productList.add(product)
                }
                adapter.updateList(productList)
            }
            .addOnFailureListener {
                Toast.makeText(this, "লোড ব্যর্থ: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun editProduct(product: Product) {
        // Open AddProductActivity with product data for editing
        val intent = Intent(this, AddProductActivity::class.java)
        intent.putExtra("productId", product.productId)
        intent.putExtra("productName", product.productName)
        startActivity(intent)
    }

    private fun deleteProduct(product: Product) {
        db.collection("products")
            .document(product.productId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "পণ্য মুছে গেছে", Toast.LENGTH_SHORT).show()
                loadProducts() // Refresh the list
            }
            .addOnFailureListener {
                Toast.makeText(this, "মুছতে ব্যর্থ: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Reload list when coming back from AddProductActivity
    override fun onResume() {
        super.onResume()
        loadProducts()
    }
}
