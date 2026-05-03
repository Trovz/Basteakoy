package com.example.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderFragment : Fragment(R.layout.fragment_order) {

    private lateinit var adapter: CartAdapter
    private lateinit var db: DatabaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        // ── Sync ticket number from DB on start ───────────────────────────
        val nextTicket = db.getNextTicketNumber()
        CartManager.syncTicket(nextTicket)

        val recyclerView     = view.findViewById<RecyclerView>(R.id.orderRecyclerView)
        val orderItemCount   = view.findViewById<TextView>(R.id.orderItemCount)
        val orderTotalText   = view.findViewById<TextView>(R.id.orderTotalText)
        val grandTotalText   = view.findViewById<TextView>(R.id.grandTotalText)
        val emptyText        = view.findViewById<TextView>(R.id.emptyText)
        val ticketNumberText = view.findViewById<TextView>(R.id.ticketNumberText)
        val checkoutButton   = view.findViewById<Button>(R.id.checkoutButton)
        val clearButton      = view.findViewById<Button>(R.id.clearOrderButton)

        fun refreshUI() {
            val items = CartManager.getItems()

            if (items.isEmpty()) {
                emptyText.visibility    = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyText.visibility    = View.GONE
                recyclerView.visibility = View.VISIBLE
            }

            ticketNumberText.text   = "Ticket #${CartManager.currentTicketNumber}"
            orderItemCount.text     = "${CartManager.getItemCount()} items in cart"
            orderTotalText.text     = "Total: ₱%.2f".format(CartManager.getTotalSales())
            grandTotalText.text     = "₱%.2f".format(CartManager.getTotalSales())
        }

       val adapter = CartAdapter(CartManager.getMutableItems()) { refreshUI() }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        refreshUI()

        // CHECKOUT
        checkoutButton.setOnClickListener {
            if (CartManager.isEmpty()) {
                Toast.makeText(requireContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Snapshot cart BEFORE checkout() clears it
            val orderTotal    = CartManager.getCartTotal()
            val itemsSnapshot = CartManager.getItems().map { lineItem ->
                OrderLineItem(
                    name     = lineItem.menuItem.name,
                    size     = lineItem.size,
                    price    = lineItem.price,
                    quantity = lineItem.quantity
                )
            }
            val ticketNum = CartManager.currentTicketNumber

            showCashChangeDialog(orderTotal) { cashGiven, change ->
                // Finalize in CartManager (in-memory history)
                CartManager.checkout()

                // Actually save to SQLite DB (was commented out before)
                val prefs     = requireContext().getSharedPreferences(
                    "basteakoy_prefs", Context.MODE_PRIVATE)
                val cashierId = prefs.getString("cashier_id", "unknown") ?: "unknown"
                val dateStr   = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(Date())

                val txn = TransactionRecord(
                    ticketNumber = ticketNum,
                    cashierId    = cashierId,
                    total        = orderTotal,       // ← correct: pre-checkout total
                    date         = dateStr,
                    cashTendered = cashGiven,
                    changeAmount = change,
                    items        = itemsSnapshot      // ← correct: pre-checkout snapshot
                )
                db.saveTransaction(txn)              // ← actually saves to DB now

                adapter.syncWithCart()
                refreshUI()

                Toast.makeText(
                    requireContext(),
                    "✅ Ticket #$ticketNum | Change: ₱%.2f".format(change),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        clearButton.setOnClickListener {
            CartManager.clearCart()
            adapter.clearAll()
            refreshUI()
        }
    }

    //  Cash Change Dialog
    private fun showCashChangeDialog(
        orderTotal: Double,
        onConfirm: (cashGiven: Double, change: Double) -> Unit
    ) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_cash_change)
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        val tvTotal    = dialog.findViewById<TextView>(R.id.dialogOrderTotal)
        val tilCash    = dialog.findViewById<TextInputLayout>(R.id.tilCashReceived)
        val etCash     = dialog.findViewById<TextInputEditText>(R.id.etCashReceived)
        val tvChange   = dialog.findViewById<TextView>(R.id.tvChangeAmount)
        val tvWarn     = dialog.findViewById<TextView>(R.id.tvInsufficientWarning)
        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirmPayment)
        val btnCancel  = dialog.findViewById<Button>(R.id.btnCancelPayment)
        val btn50      = dialog.findViewById<Button>(R.id.btn50)
        val btn100     = dialog.findViewById<Button>(R.id.btn100)
        val btn200     = dialog.findViewById<Button>(R.id.btn200)
        val btn500     = dialog.findViewById<Button>(R.id.btn500)
        val btn1000    = dialog.findViewById<Button>(R.id.btn1000)
        val btnExact   = dialog.findViewById<Button>(R.id.btnExact)

        // Show the correct pre-checkout total
        tvTotal.text = "₱%.2f".format(orderTotal)

        fun setQuickAmount(amount: Double) {
            etCash.setText("%.2f".format(amount))
            etCash.setSelection(etCash.text?.length ?: 0)
        }

        // Live change = cash - orderTotal (correct formula)
        fun updateChange() {
            val cash   = etCash.text.toString().toDoubleOrNull() ?: 0.0
            val change = cash - orderTotal
            tilCash.error     = null
            tvWarn.visibility = View.GONE

            when {
                cash == 0.0  -> {
                    tvChange.text = "₱0.00"
                    tvChange.setTextColor(android.graphics.Color.parseColor("#388E3C"))
                }
                change < 0   -> {
                    tvChange.text = "— ₱%.2f short".format(-change)
                    tvChange.setTextColor(android.graphics.Color.parseColor("#D32F2F"))
                    tvWarn.visibility = View.VISIBLE
                }
                else -> {
                    tvChange.text = "₱%.2f".format(change)
                    tvChange.setTextColor(android.graphics.Color.parseColor("#388E3C"))
                }
            }
        }

        etCash.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = updateChange()
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        })

        btn50.setOnClickListener   { setQuickAmount(50.0)  }
        btn100.setOnClickListener  { setQuickAmount(100.0) }
        btn200.setOnClickListener  { setQuickAmount(200.0) }
        btn500.setOnClickListener  { setQuickAmount(500.0) }
        btn1000.setOnClickListener { setQuickAmount(1000.0)}
        btnExact.setOnClickListener{ setQuickAmount(orderTotal) }
        btnCancel.setOnClickListener { dialog.dismiss() }

        btnConfirm.setOnClickListener {
            val cash = etCash.text.toString().toDoubleOrNull()
            if (cash == null || cash <= 0) {
                tilCash.error = "Enter cash received"; return@setOnClickListener
            }
            val change = cash - orderTotal
            if (change < 0) {
                tilCash.error = "Cash is less than total (₱%.2f)".format(orderTotal)
                return@setOnClickListener
            }
            dialog.dismiss()
            onConfirm(cash, change)
        }

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) adapter.syncWithCart()
    }
}
