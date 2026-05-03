package com.example.fragment

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup
import android.widget.ImageButton;
import android.widget.ImageView
import android.widget.RadioGroup;
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MenuAdapter(
    private var items: List<MenuItemData>,
    private val onAdd: (MenuItemData, String, Double) -> Unit
) : RecyclerView.Adapter<MenuAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img      : ImageView   = v.findViewById(R.id.foodImage)
        val name     : TextView    = v.findViewById(R.id.food_name)
        val desc     : TextView    = v.findViewById(R.id.foodDescription)
        val price    : TextView    = v.findViewById(R.id.foodPrice)
        val sizeGrp  : RadioGroup  = v.findViewById(R.id.sizeGroup)
        val addBtn   : ImageButton = v.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = items[pos]
        h.name.text = item.name
        h.desc.text = item.description

        // Image
        val imgRes = resolveImage(h.itemView, item.imageName)
        h.img.setImageResource(imgRes)

        if (item.hasSizes) {
            h.sizeGrp.visibility = View.VISIBLE
            h.sizeGrp.check(R.id.sizeMedium)
            h.price.text = "₱%.2f".format(item.priceM)
            h.sizeGrp.setOnCheckedChangeListener { _, id ->
                val sz = when (id) { R.id.sizeSmall -> "S"; R.id.sizeLarge -> "L"; else -> "M" }
                h.price.text = "₱%.2f".format(item.priceForSize(sz))
            }
        } else {
            h.sizeGrp.visibility = View.GONE
            h.price.text = "₱%.2f".format(item.priceReg)
        }

        h.addBtn.setOnClickListener {
            val (sz, pr) = if (item.hasSizes) {
                val k = when (h.sizeGrp.checkedRadioButtonId) {
                    R.id.sizeSmall -> "S"; R.id.sizeLarge -> "L"; else -> "M"
                }
                k to item.priceForSize(k)
            } else "Regular" to item.priceReg
            onAdd(item, sz, pr)
        }
    }

    fun updateList(newItems: List<MenuItemData>) {
        items = newItems; notifyDataSetChanged()
    }

    private fun resolveImage(v: View, name: String): Int {
        val ctx = v.context
        val res = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)
        return if (res != 0) res else R.drawable.baseline_fastfood_24
    }
}
