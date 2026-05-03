package com.example.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//  Uses BestSellerItem (from DB query) instead of CartLineItem
class MonthlySalesAdapter(
    private val salesList: List<BestSellerItem>
) : RecyclerView.Adapter<MonthlySalesAdapter.SalesViewHolder>() {

    class SalesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rank        : TextView = view.findViewById(R.id.salesRank)
        val itemName    : TextView = view.findViewById(R.id.salesItemName)
        val itemSize    : TextView = view.findViewById(R.id.salesItemSize)
        val itemQuantity: TextView = view.findViewById(R.id.salesItemQuantity)
        val itemTotal   : TextView = view.findViewById(R.id.salesItemTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sales, parent, false)
        return SalesViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalesViewHolder, position: Int) {
        val sale = salesList[position]
        holder.rank.text         = "#${position + 1}"
        holder.itemName.text     = sale.name
        holder.itemSize.text     = if (sale.size.isNotEmpty()) "Size: ${sale.size}" else "Regular"
        holder.itemQuantity.text = "Qty Sold: ${sale.totalQty}"
        holder.itemTotal.text    = "₱%.2f".format(sale.totalRev)
    }

    override fun getItemCount() = salesList.size
}
