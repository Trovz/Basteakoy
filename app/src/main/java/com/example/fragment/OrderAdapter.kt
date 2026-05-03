package com.example.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(
    private val itemList: List<MenuItem>,
    private val onAddToCart: (MenuItem, String, Double) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foodImage: ImageView = view.findViewById(R.id.foodImage)
        val nameText: TextView = view.findViewById(R.id.food_name)
        val descText: TextView = view.findViewById(R.id.foodDescription)
        val priceText: TextView = view.findViewById(R.id.foodPrice)
        val sizeGroup: RadioGroup = view.findViewById(R.id.sizeGroup)
        val orderButton: ImageButton = view.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = itemList[position]

        holder.foodImage.setImageResource(item.imageRes)
        holder.nameText.text = item.name
        holder.descText.text = item.description

        // Check if item has sizes or fixed price
        val hasSizes = item.prices.keys.any { it in listOf("S", "M", "L") }

        if (hasSizes) {
            holder.sizeGroup.visibility = View.VISIBLE
            holder.priceText.text = "₱%.2f".format(item.prices["M"] ?: 0.0)

            holder.sizeGroup.setOnCheckedChangeListener { _, checkedId ->
                val selectedSize = when (checkedId) {
                    R.id.sizeSmall  -> "S"
                    R.id.sizeMedium -> "M"
                    R.id.sizeLarge  -> "L"
                    else -> "M"
                }
                holder.priceText.text = "₱%.2f".format(item.prices[selectedSize] ?: 0.0)
            }
        } else {
            // Hide size selector for Soda and Snacks
            holder.sizeGroup.visibility = View.GONE
            holder.priceText.text = "₱%.2f".format(item.prices["Regular"] ?: 0.0)
        }

        holder.orderButton.setOnClickListener {
            val selectedSize = if (hasSizes) {
                when (holder.sizeGroup.checkedRadioButtonId) {
                    R.id.sizeSmall  -> "S"
                    R.id.sizeMedium -> "M"
                    R.id.sizeLarge  -> "L"
                    else -> "M"
                }
            } else {
                "Regular"
            }
            val price = item.prices[selectedSize] ?: 0.0
            onAddToCart(item, selectedSize, price)
        }
    }
    override fun getItemCount(): Int = itemList.size

}