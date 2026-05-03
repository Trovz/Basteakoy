package com.example.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var db: DatabaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        val nameText       = view.findViewById<TextView>(R.id.profileName)
        val roleText       = view.findViewById<TextView>(R.id.profileRole)
        val emailText      = view.findViewById<TextView>(R.id.profileEmail)
        val phoneText      = view.findViewById<TextView>(R.id.profilePhone)
        val shiftText      = view.findViewById<TextView>(R.id.profileShift)
        val totalOrdersTxt = view.findViewById<TextView>(R.id.totalOrdersText)
        val totalSalesTxt  = view.findViewById<TextView>(R.id.totalSalesText)
        val btnLogout      = view.findViewById<Button>(R.id.btnLogout)

        val prefs = requireContext().getSharedPreferences("basteakoy_prefs", Context.MODE_PRIVATE)
        val name  = prefs.getString("emp_name",  "Cashier")       ?: "Cashier"
        val role  = prefs.getString("emp_role",  "cashier")       ?: "cashier"
        val email = prefs.getString("emp_email", "—")             ?: "—"
        val phone = prefs.getString("emp_phone", "—")             ?: "—"
        val shift = prefs.getString("emp_shift", "Morning Shift") ?: "Morning Shift"

        nameText.text  = name
        roleText.text  = role.replaceFirstChar { it.uppercase() }
        emailText.text = email
        phoneText.text = phone
        shiftText.text = shift

        // Read today's stats from SQLite DB, not in-memory CartManager
        loadTodayStats(totalOrdersTxt, totalSalesTxt)

        btnLogout.setOnClickListener {
            prefs.edit()
                .putBoolean("is_logged_in", false)
                .remove("cashier_id").remove("emp_name").remove("emp_role")
                .remove("emp_email").remove("emp_phone").remove("emp_shift")
                .apply()
            CartManager.clearCart()

            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        view?.let { v ->
            loadTodayStats(
                v.findViewById(R.id.totalOrdersText),
                v.findViewById(R.id.totalSalesText)
            )
        }
    }

    private fun loadTodayStats(ordersTxt: TextView, salesTxt: TextView) {
        //  Use today's date prefix to query DB
        val datePrefix = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val orders = db.getTodayOrderCount(datePrefix)
        val sales  = db.getTodaySalesTotal(datePrefix)
        ordersTxt.text = "$orders"
        salesTxt.text  = "₱%.2f".format(sales)
    }
}
