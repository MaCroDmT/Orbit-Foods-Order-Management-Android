package com.orbitfoods.ordersheet.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.orbitfoods.ordersheet.R

// Simple data holder for displaying an order in the list
data class OrderSummary(
    val orderId: String,
    val serialNumber: Long,
    val date: String,
    val customerName: String,
    val address: String,
    val totalAmount: Double
)

class OrderAdapter(
    private var orders: MutableList<OrderSummary>,
    private val onView: (OrderSummary) -> Unit,
    private val onDelete: (OrderSummary) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSerial: TextView = itemView.findViewById(R.id.tvSerialNumber)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvName: TextView = itemView.findViewById(R.id.tvCustomerName)
        val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        val btnView: Button = itemView.findViewById(R.id.btnView)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvSerial.text = "ক্রমিক নং: ${order.serialNumber}"
        holder.tvDate.text = order.date
        holder.tvName.text = order.customerName
        holder.tvAddress.text = order.address
        holder.tvTotal.text = "মোট: ৳ ${"%.2f".format(order.totalAmount)}"
        holder.btnView.setOnClickListener { onView(order) }
        holder.btnDelete.setOnClickListener { onDelete(order) }
    }

    override fun getItemCount() = orders.size

    fun updateList(newOrders: MutableList<OrderSummary>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
