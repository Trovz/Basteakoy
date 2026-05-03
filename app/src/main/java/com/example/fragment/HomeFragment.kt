package com.example.fragment

import android.os.Bundle
import android.text.Editable;
import android.text.TextWatcher
import android.view.View
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var db: DatabaseHelper
    private lateinit var adapter: MenuAdapter
    private var currentCategory = "All"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        val recyclerView = view.findViewById<RecyclerView>(R.id.homeRecyclerView)
        val searchBar    = view.findViewById<EditText>(R.id.searchBar)
        val greeting     = view.findViewById<TextView>(R.id.greetingText)
        val chips = listOf(
            view.findViewById<TextView>(R.id.btnAll),
            view.findViewById<TextView>(R.id.btnFruits),
            view.findViewById<TextView>(R.id.btnPizza),
            view.findViewById<TextView>(R.id.btnSoda),
            view.findViewById<TextView>(R.id.btnSnacks)
        )

        val prefs = requireContext().getSharedPreferences("basteakoy_prefs", android.content.Context.MODE_PRIVATE)
        greeting.text = "Good Day, ${prefs.getString("emp_name", "Cashier")}! 👋"

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = MenuAdapter(emptyList()) { item, size, price ->
            CartManager.addItem(item, size, price)
            Toast.makeText(requireContext(),
                "✅ ${item.name} ($size) ₱%.2f added!".format(price), Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        fun loadItems(category: String, query: String = "") {
            currentCategory = category
            val items = when {
                query.isNotEmpty() -> db.searchMenu(query)
                category == "All"  -> db.getAllMenuItems()
                else               -> db.getMenuByCategory(category)
            }
            adapter.updateList(items)
        }

        fun selectChip(idx: Int) {
            chips.forEachIndexed { i, chip ->
                if (i == idx) {
                    chip.background = requireContext().getDrawable(R.drawable.chip_selected)
                    chip.setTextColor(android.graphics.Color.WHITE)
                } else {
                    chip.background = requireContext().getDrawable(R.drawable.chip_unselected)
                    chip.setTextColor(android.graphics.Color.parseColor("#FFBB00"))
                }
            }
        }

        val cats = listOf("All","Fruits","Pizza","Soda","Snacks")
        chips.forEachIndexed { i, chip ->
            chip.setOnClickListener { selectChip(i); loadItems(cats[i]) }
        }

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { loadItems(currentCategory, s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        })

        selectChip(0); loadItems("All")
    }

    override fun onResume() {
        super.onResume()
        // Refresh after adding a product
        val items = if (currentCategory == "All") db.getAllMenuItems()
                    else db.getMenuByCategory(currentCategory)
        if (::adapter.isInitialized) adapter.updateList(items)
    }
}
