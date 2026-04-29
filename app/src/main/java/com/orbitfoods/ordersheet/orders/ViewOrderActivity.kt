package com.orbitfoods.ordersheet.orders

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.orbitfoods.ordersheet.databinding.ActivityViewOrderBinding
import com.orbitfoods.ordersheet.models.Order
import com.orbitfoods.ordersheet.models.OrderItem
import com.orbitfoods.ordersheet.pdf.PdfGenerator
import java.io.File

class ViewOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewOrderBinding
    private val db = FirebaseFirestore.getInstance()
    private var currentOrder: Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "অর্ডার বিবরণ"

        val orderId = intent.getStringExtra("orderId") ?: ""
        if (orderId.isEmpty()) {
            Toast.makeText(this, "অর্ডার পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadOrder(orderId)

        binding.btnCustomerCopy.setOnClickListener {
            val order = currentOrder
            if (order == null) {
                Toast.makeText(this, "অর্ডার লোড হয়নি", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                val file = PdfGenerator(this).generatePdf(order, isOfficeCopy = false)
                openPdf(file)
            } catch (e: Exception) {
                Toast.makeText(this, "PDF ব্যর্থ: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnOfficeCopy.setOnClickListener {
            val order = currentOrder
            if (order == null) {
                Toast.makeText(this, "অর্ডার লোড হয়নি", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                val file = PdfGenerator(this).generatePdf(order, isOfficeCopy = true)
                openPdf(file)
            } catch (e: Exception) {
                Toast.makeText(this, "PDF ব্যর্থ: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadOrder(orderId: String) {
        db.collection("orders")
            .document(orderId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {

                    val serial = (doc.getLong("serialNumber") ?: 0).toInt()
                    val date = doc.getString("date") ?: ""
                    val name = doc.getString("customerName") ?: ""
                    val address = doc.getString("address") ?: ""
                    val total = doc.getDouble("totalAmount") ?: 0.0

                    // Parse product list
                    val rawList = doc.get("productList") as? List<*> ?: emptyList<Any>()
                    val items = rawList.mapNotNull { raw ->
                        val map = raw as? Map<*, *> ?: return@mapNotNull null
                        OrderItem(
                            productName = map["productName"] as? String ?: "",
                            quantity = (map["quantity"] as? Double) ?: 0.0,
                            rate = (map["rate"] as? Double) ?: 0.0,
                            amount = (map["amount"] as? Double) ?: 0.0
                        )
                    }

                    // Build Order object
                    currentOrder = Order(
                        orderId = orderId,
                        serialNumber = serial,
                        date = date,
                        customerName = name,
                        address = address,
                        productList = items,
                        totalAmount = total
                    )

                    // Update screen
                    binding.tvSerial.text = "ক্রমিক নং: $serial"
                    binding.tvDate.text = "তারিখ: $date"
                    binding.tvName.text = "নাম: $name"
                    binding.tvAddress.text = "ঠিকানা: $address"
                    binding.tvTotal.text = "৳ ${"%.2f".format(total)}"
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "লোড ব্যর্থ: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openPdf(file: File) {
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }
}
