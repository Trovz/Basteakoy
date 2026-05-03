package com.example.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SalesAdapter(private val cartItems: List<CartItem>) :
    RecyclerView.Adapter<SalesAdapter.SalesViewHolder>() {

    class SalesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.salesItemName)
        val itemSize: TextView = view.findViewById(R.id.salesItemSize)
        val itemQuantity: TextView = view.findViewById(R.id.salesItemQuantity)
        val itemTotal: TextView = view.findViewById(R.id.salesItemTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sales, parent, false)
        return SalesViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalesViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.itemName.text = cartItem.menuItem.name
        holder.itemSize.text = "Size: ${cartItem.size}"
        holder.itemQuantity.text = "Qty: ${cartItem.quantity}"
        holder.itemTotal.text = "₱%.2f".format(cartItem.price * cartItem.quantity)
    }

    override fun getItemCount() = cartItems.size
}