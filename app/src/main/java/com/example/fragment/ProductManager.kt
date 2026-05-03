package com.example.fragment

object ProductManager {
    private val products = mutableListOf<MenuItem>()

    fun addProduct(item: MenuItem) = products.add(item)

    fun getProducts(): List<MenuItem> = products

    fun removeProduct(item: MenuItem) = products.remove(item)

    fun getAllItems(defaultItems: List<MenuItem>): List<MenuItem> {
        return defaultItems + products
    }
}