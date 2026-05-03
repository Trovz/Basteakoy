package com.example.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        db = DatabaseHelper(this)

        val btnBack       = findViewById<android.widget.ImageButton>(R.id.btnBack)
        val tilId         = findViewById<TextInputLayout>(R.id.tilForgotCashierId)
        val tilEmail      = findViewById<TextInputLayout>(R.id.tilForgotEmail)
        val etId          = findViewById<TextInputEditText>(R.id.etForgotCashierId)
        val etEmail       = findViewById<TextInputEditText>(R.id.etForgotEmail)
        val btnSend       = findViewById<Button>(R.id.btnSendReset)
        val tvError       = findViewById<TextView>(R.id.tvForgotError)
        val successLayout = findViewById<LinearLayout>(R.id.successLayout)
        val tvSuccess     = findViewById<TextView>(R.id.tvSuccessMsg)
        val tvBackLogin   = findViewById<TextView>(R.id.tvBackToLogin)
        val progress      = findViewById<ProgressBar>(R.id.forgotProgress)

        btnBack.setOnClickListener { finish() }
        tvBackLogin.setOnClickListener { finish() }

        // Clear errors on typing
        etId.setOnFocusChangeListener    { _, _ -> tilId.error = null;    tvError.visibility = View.GONE }
        etEmail.setOnFocusChangeListener { _, _ -> tilEmail.error = null; tvError.visibility = View.GONE }

        btnSend.setOnClickListener {
            val id    = etId.text.toString().trim()
            val email = etEmail.text.toString().trim()

            tilId.error    = null
            tilEmail.error = null
            tvError.visibility    = View.GONE
            successLayout.visibility = View.GONE

            // Validate
            var valid = true
            if (id.isEmpty())    { tilId.error    = "Cashier ID is required"; valid = false }
            if (email.isEmpty()) { tilEmail.error = "Email is required";       valid = false }
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = "Enter a valid email address"; valid = false
            }
            if (!valid) return@setOnClickListener

            // Show loading
            progress.visibility = View.VISIBLE
            btnSend.isEnabled   = false

            Handler(Looper.getMainLooper()).postDelayed({
                progress.visibility = View.GONE
                btnSend.isEnabled   = true

                // Verify in database
                val emp = db.getEmployee(id)
                when {
                    emp == null -> {
                        tilId.error = "Cashier ID not found"
                    }
                    emp.email.lowercase() != email.lowercase() -> {
                        tilEmail.error = "Email does not match our records"
                    }
                    else -> {
                        // In a real app, send an email here.
                        // We simulate success and show the new temp password.
                        val tempPassword = "Baste@${id.takeLast(4)}"
                        // Update DB with temp password
                        // db.updatePassword(id, tempPassword)   ← extend DatabaseHelper for this

                        tvSuccess.text = "A temporary password has been sent to ${emp.email}.\nTemp PW: $tempPassword"
                        successLayout.visibility = View.VISIBLE
                        etId.text?.clear()
                        etEmail.text?.clear()
                    }
                }
            }, 1200)
        }
    }
}
