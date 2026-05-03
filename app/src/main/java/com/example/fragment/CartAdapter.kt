package com.example.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    private val items: MutableList<CartLineItem>,
    private val onChange: () -> Unit
) : RecyclerView.Adapter<CartAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img  : ImageView   = v.findViewById(R.id.orderItemImage)
        val name : TextView    = v.findViewById(R.id.orderItemName)
        val size : TextView    = v.findViewById(R.id.orderItemSize)
        val price: TextView    = v.findViewById(R.id.orderItemPrice)
        val qty  : TextView    = v.findViewById(R.id.orderItemQuantity)
        val minus: ImageButton = v.findViewById(R.id.btnMinus)
        val plus : ImageButton = v.findViewById(R.id.btnPlus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, vt: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(h: VH, pos: Int) {
        val lineItem = items[pos]
        val ctx = h.itemView.context
        val imgRes = ctx.resources.getIdentifier(
            lineItem.menuItem.imageName, "drawable", ctx.packageName
        )
        h.img.setImageResource(if (imgRes != 0) imgRes else R.drawable.baseline_fastfood_24)
        h.name.text  = lineItem.menuItem.name
        h.size.text  = "Size: ${lineItem.size}"
        h.price.text = "₱%.2f".format(lineItem.subtotal)
        h.qty.text   = lineItem.quantity.toString()

        h.plus.setOnClickListener {
            lineItem.quantity++
            notifyItemChanged(h.adapterPosition)
            onChange()
        }

        h.minus.setOnClickListener {
            if (lineItem.quantity > 1) {
                lineItem.quantity--
                notifyItemChanged(h.adapterPosition)
            } else {
                val p = h.adapterPosition
                CartManager.removeItem(lineItem)
                items.removeAt(p)
                notifyItemRemoved(p)
            }
            onChange()
        }
    }

    fun syncWithCart() {
        items.clear()
        items.addAll(CartManager.getMutableItems())
        notifyDataSetChanged()
    }

    fun clearAll() {
        items.clear()
        notifyDataSetChanged()
    }
}
