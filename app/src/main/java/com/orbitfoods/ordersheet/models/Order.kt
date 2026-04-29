package com.orbitfoods.ordersheet.models

data class Order(
    val orderId: String = "",
    val serialNumber: Int = 0,
    val date: String = "",
    val customerName: String = "",
    val address: String = "",
    val productList: List<OrderItem> = emptyList(),
    val totalAmount: Double = 0.0
)

data class OrderItem(
    val productName: String = "",
    val quantity: Double = 0.0,
    val rate: Double = 0.0,
    val amount: Double = 0.0
)