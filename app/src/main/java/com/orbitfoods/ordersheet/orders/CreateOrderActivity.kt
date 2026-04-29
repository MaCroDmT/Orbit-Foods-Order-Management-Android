package com.orbitfoods.ordersheet.orders

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.orbitfoods.ordersheet.R
import com.orbitfoods.ordersheet.databinding.ActivityCreateOrderBinding

class CreateOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateOrderBinding
    private val db = FirebaseFirestore.getInstance()
    private val productNames = mutableListOf<String>()
    private var isProductsLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "নতুন অর্ডার"

        binding.btnAddProduct.isEnabled = false
        binding.btnAddProduct.text = "পণ্য লোড হচ্ছে..."
        binding.btnSaveOrder.isEnabled = false

        loadProductsFromFirebase()

        binding.btnAddProduct.setOnClickListener {
            if (isProductsLoaded) {
                addProductRow()
            } else {
                Toast.makeText(this,
                    "পণ্য লোড হচ্ছে, অপেক্ষা করুন",
                    Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSaveOrder.setOnClickListener {
            saveOrder()
        }
    }

    private fun loadProductsFromFirebase() {
        db.collection("products")
            .get()
            .addOnSuccessListener { documents ->
                productNames.clear()
                for (doc in documents) {
                    val name = doc.getString("productName") ?: ""
                    if (name.isNotEmpty()) productNames.add(name)
                }

                isProductsLoaded = true
                binding.btnAddProduct.isEnabled = true
                binding.btnAddProduct.text = "+ নতুন পণ্য যোগ করুন"
                binding.btnSaveOrder.isEnabled = true

                if (productNames.isEmpty()) {
                    Toast.makeText(this,
                        "কোনো পণ্য নেই। আগে পণ্য যোগ করুন।",
                        Toast.LENGTH_LONG).show()
                } else {
                    addProductRow()
                }
            }
            .addOnFailureListener { e ->
                isProductsLoaded = true
                binding.btnAddProduct.isEnabled = true
                binding.btnAddProduct.text = "+ নতুন পণ্য যোগ করুন"
                binding.btnSaveOrder.isEnabled = true
                Toast.makeText(this,
                    "পণ্য লোড ব্যর্থ: ${e.message}",
                    Toast.LENGTH_LONG).show()
            }
    }

    private fun addProductRow() {
        if (productNames.isEmpty()) {
            Toast.makeText(this,
                "কোনো পণ্য পাওয়া যায়নি",
                Toast.LENGTH_SHORT).show()
            return
        }

        val rowView = layoutInflater.inflate(
            R.layout.item_order_product,
            binding.productContainer,
            false
        )

        val spinner = rowView.findViewById<Spinner>(R.id.spinnerProduct)
        val etQty = rowView.findViewById<EditText>(R.id.etQuantity)
        val etRate = rowView.findViewById<EditText>(R.id.etRate)
        val etDiscount = rowView.findViewById<EditText>(R.id.etDiscount)
        val tvAmount = rowView.findViewById<TextView>(R.id.tvAmount)
        val tvDiscountInfo = rowView.findViewById<TextView>(R.id.tvDiscountInfo)
        val btnRemove = rowView.findViewById<Button>(R.id.btnRemove)

        // BLACK background + WHITE text spinner adapter
        val spinnerAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item,         // custom white text layout
            productNames.toList()
        )
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown)
        spinner.adapter = spinnerAdapter

        // Calculate amount with discount
        fun recalculate() {
            val qty = etQty.text.toString().toDoubleOrNull() ?: 0.0
            val rate = etRate.text.toString().toDoubleOrNull() ?: 0.0
            val discount = etDiscount.text.toString().toDoubleOrNull() ?: 0.0

            val rawAmount = qty * rate
            val discountAmount = rawAmount * (discount / 100.0)
            val finalAmount = rawAmount - discountAmount

            tvAmount.text = "৳ ${"%.2f".format(finalAmount)}"

            // Show discount info label
            if (discount > 0.0) {
                tvDiscountInfo.visibility = View.VISIBLE
                tvDiscountInfo.text = "-${"%.2f".format(discountAmount)} (${discount}% ছাড়)"
            } else {
                tvDiscountInfo.visibility = View.GONE
            }

            calculateTotal()
        }

        // TextWatcher for all 3 fields
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { recalculate() }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        }

        etQty.addTextChangedListener(watcher)
        etRate.addTextChangedListener(watcher)
        etDiscount.addTextChangedListener(watcher)

        btnRemove.setOnClickListener {
            binding.productContainer.removeView(rowView)
            calculateTotal()
        }

        binding.productContainer.addView(rowView)
    }

    private fun calculateTotal(): Double {
        var total = 0.0
        for (i in 0 until binding.productContainer.childCount) {
            val row = binding.productContainer.getChildAt(i)
            val etQty = row.findViewById<EditText>(R.id.etQuantity)
            val etRate = row.findViewById<EditText>(R.id.etRate)
            val etDiscount = row.findViewById<EditText>(R.id.etDiscount)

            val qty = etQty?.text?.toString()?.toDoubleOrNull() ?: 0.0
            val rate = etRate?.text?.toString()?.toDoubleOrNull() ?: 0.0
            val discount = etDiscount?.text?.toString()?.toDoubleOrNull() ?: 0.0

            val raw = qty * rate
            val finalAmount = raw - (raw * discount / 100.0)
            total += finalAmount
        }
        binding.tvTotal.text = "৳ ${"%.2f".format(total)}"
        return total
    }

    private fun collectProductRows(): List<HashMap<String, Any>> {
        val items = mutableListOf<HashMap<String, Any>>()
        for (i in 0 until binding.productContainer.childCount) {
            val row = binding.productContainer.getChildAt(i)
            val spinner = row.findViewById<Spinner>(R.id.spinnerProduct)
            val etQty = row.findViewById<EditText>(R.id.etQuantity)
            val etRate = row.findViewById<EditText>(R.id.etRate)
            val etDiscount = row.findViewById<EditText>(R.id.etDiscount)

            val name = spinner?.selectedItem?.toString() ?: ""
            val qty = etQty?.text?.toString()?.toDoubleOrNull() ?: 0.0
            val rate = etRate?.text?.toString()?.toDoubleOrNull() ?: 0.0
            val discount = etDiscount?.text?.toString()?.toDoubleOrNull() ?: 0.0
            val raw = qty * rate
            val amount = raw - (raw * discount / 100.0)

            if (name.isNotEmpty() && qty > 0) {
                items.add(hashMapOf(
                    "productName" to name,
                    "quantity" to qty,
                    "rate" to rate,
                    "discount" to discount,
                    "amount" to amount
                ))
            }
        }
        return items
    }

    private fun saveOrder() {
        val date = binding.etDate.text.toString().trim()
        val name = binding.etName.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()

        if (date.isEmpty() || name.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "সব তথ্য পূরণ করুন", Toast.LENGTH_SHORT).show()
            return
        }

        val productRows = collectProductRows()
        if (productRows.isEmpty()) {
            Toast.makeText(this,
                "অন্তত একটি পণ্য যোগ করুন",
                Toast.LENGTH_SHORT).show()
            return
        }

        val total = calculateTotal()

        binding.btnSaveOrder.isEnabled = false
        binding.btnSaveOrder.text = "সংরক্ষণ হচ্ছে..."

        db.collection("orders").get()
            .addOnSuccessListener { docs ->
                val nextSerial = docs.size() + 1
                val order = hashMapOf(
                    "serialNumber" to nextSerial,
                    "date" to date,
                    "customerName" to name,
                    "address" to address,
                    "productList" to productRows,
                    "totalAmount" to total
                )
                db.collection("orders").add(order)
                    .addOnSuccessListener { docRef ->
                        Toast.makeText(this,
                            "অর্ডার সংরক্ষিত! ক্রমিক নং: $nextSerial",
                            Toast.LENGTH_LONG).show()
                        val intent = Intent(this, ViewOrderActivity::class.java)
                        intent.putExtra("orderId", docRef.id)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        binding.btnSaveOrder.isEnabled = true
                        binding.btnSaveOrder.text = "অর্ডার সংরক্ষণ করুন"
                        Toast.makeText(this,
                            "সংরক্ষণ ব্যর্থ: ${it.message}",
                            Toast.LENGTH_SHORT).show()
                    }
            }
    }
}

