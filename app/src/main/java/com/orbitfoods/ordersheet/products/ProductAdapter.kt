package com.orbitfoods.ordersheet.products

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.orbitfoods.ordersheet.R
import com.orbitfoods.ordersheet.models.Product

class ProductAdapter(
    private var products: MutableList<Product>,
    private val onEdit: (Product) -> Unit,    // called when Edit clicked
    private val onDelete: (Product) -> Unit   // called when Delete clicked
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // This holds references to the views in each row
    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    // Creates one row view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    // Fills data into each row
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        // Set the product name text
        holder.tvName.text = product.productName

        // Edit button click
        holder.btnEdit.setOnClickListener {
            onEdit(product)
        }

        // Delete button click
        holder.btnDelete.setOnClickListener {
            onDelete(product)
        }
    }

    // How many rows total
    override fun getItemCount() = products.size

    // Call this to refresh the list
    fun updateList(newProducts: MutableList<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}