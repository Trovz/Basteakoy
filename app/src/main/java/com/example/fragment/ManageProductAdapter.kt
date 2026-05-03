package com.example.fragment

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ManageProductAdapter(
    private val items: MutableList<MenuItemData>,
    private val onDelete: (MenuItemData, Int) -> Unit
) : RecyclerView.Adapter<ManageProductAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img     : ImageView  = v.findViewById(R.id.manageItemImage)
        val name    : TextView   = v.findViewById(R.id.manageItemName)
        val category: TextView   = v.findViewById(R.id.manageItemCategory)
        val price   : TextView   = v.findViewById(R.id.manageItemPrice)
        val btnDel  : ImageButton= v.findViewById(R.id.btnDeleteProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, vt: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_manage_product, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = items[pos]
        h.name.text     = item.name
        h.category.text = item.category

        // Price display
        h.price.text = if (item.hasSizes)
            "S:₱${item.priceS} M:₱${item.priceM} L:₱${item.priceL}"
        else
            "₱%.2f".format(item.priceReg)

        // Image — try URI first, then drawable name
        try {
            val uri = Uri.parse(item.imageName)
            if (uri.scheme == "content" || uri.scheme == "file") {
                h.img.setImageURI(uri)
            } else throw Exception("not a URI")
        } catch (e: Exception) {
            val ctx = h.itemView.context
            val res = ctx.resources.getIdentifier(item.imageName, "drawable", ctx.packageName)
            h.img.setImageResource(if (res != 0) res else R.drawable.baseline_fastfood_24)
        }

        h.btnDel.setOnClickListener { onDelete(item, h.adapterPosition) }
    }
}
