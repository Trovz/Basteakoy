package com.example.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SalesFragment : Fragment(R.layout.fragment_sales) {

    private lateinit var db: DatabaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        val recyclerView  = view.findViewById<RecyclerView>(R.id.salesRecyclerView)
        val totalText     = view.findViewById<TextView>(R.id.totalSalesText)
        val itemCountText = view.findViewById<TextView>(R.id.itemCountText)
        val monthText     = view.findViewById<TextView>(R.id.monthText)

        val monthFormat   = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthText.text    = monthFormat.format(Date())

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        refreshSales(recyclerView, totalText, itemCountText)
    }

    override fun onResume() {
        super.onResume()
        view?.let { v ->
            refreshSales(
                v.findViewById(R.id.salesRecyclerView),
                v.findViewById(R.id.totalSalesText),
                v.findViewById(R.id.itemCountText)
            )
        }
    }

    private fun refreshSales(
        recyclerView  : RecyclerView,
        totalText     : TextView,
        itemCountText : TextView
    ) {
        // Read from SQLite DB — persistent across sessions
        val datePrefix  = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        val bestSellers = db.getBestSellingItems(datePrefix)   // List<BestSellerItem>
        val monthTotal  = db.getTodaySalesTotal(datePrefix)    // month total from DB
        val totalItems  = bestSellers.sumOf { it.totalQty }

        //  Adapter now receives BestSellerItem list from DB
        recyclerView.adapter = MonthlySalesAdapter(bestSellers)

        totalText.text     = "Total Sales: ₱%.2f".format(monthTotal)
        itemCountText.text = "Total Items Sold: $totalItems"
    }
}
