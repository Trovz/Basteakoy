package com.example.fragment

object CartManager {
    private val _cart         = mutableListOf<CartLineItem>()
    private val _salesHistory = mutableListOf<CartLineItem>()
    private var _ticketCounter = 1

    // Read-only ticket number
    val currentTicketNumber: Int get() = _ticketCounter

    // Called once on app start to sync ticket number from DB
    fun syncTicket(nextTicket: Int) {
        if (nextTicket > _ticketCounter) _ticketCounter = nextTicket
    }

    // Cart operations
    fun addItem(item: MenuItemData, size: String, price: Double) {
        val existing = _cart.find { it.menuItem.id == item.id && it.size == size }
        if (existing != null) existing.quantity++
        else _cart.add(CartLineItem(item, size, price))
    }

    fun removeItem(item: CartLineItem) = _cart.remove(item)
    fun clearCart() = _cart.clear()

    fun getItems(): List<CartLineItem> = _cart.toList()
    fun getMutableItems(): MutableList<CartLineItem> = _cart

    fun getItemCount(): Int = _cart.sumOf { it.quantity }
    fun getCartTotal(): Double = _cart.sumOf { it.subtotal }
    fun isEmpty(): Boolean = _cart.isEmpty()

    // Checkout merges to in-memory history + bumps ticket
    fun checkout(): Int {
        _cart.forEach { cartItem ->
            val existing = _salesHistory.find {
                it.menuItem.id == cartItem.menuItem.id && it.size == cartItem.size
            }
            if (existing != null) existing.quantity += cartItem.quantity
            else _salesHistory.add(
                CartLineItem(cartItem.menuItem, cartItem.size, cartItem.price, cartItem.quantity)
            )
        }
        val ticket = _ticketCounter
        _ticketCounter++
        _cart.clear()
        return ticket
    }

    //  In-memory sales (for current session badge counts)
    fun getSalesHistory(): List<CartLineItem> = _salesHistory.toList()
    fun getTotalSales(): Double               = _salesHistory.sumOf { it.subtotal }
    fun getTotalSalesCount(): Int             = _salesHistory.sumOf { it.quantity }
    fun completedOrders(): Int               = _ticketCounter - 1

    fun clearSalesHistory() = _salesHistory.clear()
}
