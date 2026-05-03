package com.example.fragment

data class CartItem(
    val menuItem: MenuItem,
    val size: String,
    val price: Double,
    var quantity: Int = 1
)