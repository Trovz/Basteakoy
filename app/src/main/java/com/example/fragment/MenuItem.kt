package com.example.fragment

data class MenuItem(
    val name: String,
    val description: String,
    val prices: Map<String, Double>,
    val imageRes: Int,
    val category: String
)