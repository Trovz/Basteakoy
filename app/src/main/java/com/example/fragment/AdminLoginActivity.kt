package com.example.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        db = DatabaseHelper(this)

        val btnBack     = findViewById<ImageButton>(R.id.btnAdminBack)
        val tilId       = findViewById<TextInputLayout>(R.id.tilAdminId)
        val tilPw       = findViewById<TextInputLayout>(R.id.tilAdminPassword)
        val etId        = findViewById<TextInputEditText>(R.id.etAdminId)
        val etPw        = findViewById<TextInputEditText>(R.id.etAdminPassword)
        val btnConfirm  = findViewById<Button>(R.id.btnAdminConfirmLogin)
        val tvError     = findViewById<TextView>(R.id.tvAdminError)
        val progress    = findViewById<ProgressBar>(R.id.adminProgress)

        btnBack.setOnClickListener { finish() }

        etId.setOnFocusChangeListener { _, _ -> tilId.error = null; tvError.visibility = View.GONE }
        etPw.setOnFocusChangeListener { _, _ -> tilPw.error = null; tvError.visibility = View.GONE }

        btnConfirm.setOnClickListener {
            val id = etId.text.toString().trim()
            val pw = etPw.text.toString().trim()

            tilId.error  = null
            tilPw.error  = null
            tvError.visibility = View.GONE

            var valid = true
            if (id.isEmpty()) { tilId.error = "Admin ID is required"; valid = false }
            if (pw.isEmpty()) { tilPw.error = "Password is required"; valid = false }
            if (!valid) return@setOnClickListener

            // Loading
            progress.visibility   = View.VISIBLE
            btnConfirm.isEnabled  = false
            etId.isEnabled        = false
            etPw.isEnabled        = false

            Handler(Looper.getMainLooper()).postDelayed({
                progress.visibility  = View.GONE
                btnConfirm.isEnabled = true
                etId.isEnabled       = true
                etPw.isEnabled       = true

                val emp = db.getEmployee(id)
                when {
                    emp == null -> {
                        tilId.error = "Admin ID not found"
                    }
                    emp.role != "admin" -> {
                        showError(tvError, "⛔ This account does not have administrator access.")
                    }
                    emp.password != pw -> {
                        tilPw.error = "Incorrect password"
                        etPw.text?.clear()
                    }
                    else -> {
                        // Save session as admin
                        getSharedPreferences("basteakoy_prefs", MODE_PRIVATE).edit()
                            .putBoolean("is_logged_in", true)
                            .putString("cashier_id",   emp.cashierId)
                            .putString("emp_name",      emp.name)
                            .putString("emp_role",      "admin")
                            .putString("emp_email",     emp.email)
                            .putString("emp_phone",     emp.phone)
                            .putString("emp_shift",     emp.shift)
                            .apply()

                        // Go to main, clear all back stack
                        val intent = Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                }
            }, 900)
        }
    }

    private fun showError(tv: TextView, msg: String) {
        tv.text = msg
        tv.visibility = View.VISIBLE
    }
}
