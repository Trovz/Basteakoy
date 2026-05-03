package com.example.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    private lateinit var tilCashierId : TextInputLayout
    private lateinit var tilPassword  : TextInputLayout
    private lateinit var etCashierId  : TextInputEditText
    private lateinit var etPassword   : TextInputEditText
    private lateinit var btnLogin     : Button
    private lateinit var btnAdmin     : Button
    private lateinit var tvForgot     : TextView
    private lateinit var tvError      : TextView
    private lateinit var progress     : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = DatabaseHelper(this)

        // Auto-login if session exists
        val prefs = getSharedPreferences("basteakoy_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("is_logged_in", false)) {
            goToMain(); return
        }

        bindViews()
        setupListeners()
    }

    private fun bindViews() {
        tilCashierId = findViewById(R.id.tilCashierId)
        tilPassword  = findViewById(R.id.tilPassword)
        etCashierId  = findViewById(R.id.etCashierId)
        etPassword   = findViewById(R.id.etPassword)
        btnLogin     = findViewById(R.id.btnLogin)
        btnAdmin     = findViewById(R.id.btnAdminLogin)
        tvForgot     = findViewById(R.id.tvForgotPassword)
        tvError      = findViewById(R.id.tvLoginError)
        progress     = findViewById(R.id.loginProgress)
    }

    private fun setupListeners() {
        // Clear errors on typing
        etCashierId.setOnFocusChangeListener { _, _ -> tilCashierId.error = null; tvError.visibility = View.GONE }
        etPassword.setOnFocusChangeListener  { _, _ -> tilPassword.error  = null; tvError.visibility = View.GONE }

        // Cashier Login
        btnLogin.setOnClickListener {
            if (validate()) performLogin(isAdmin = false)
        }

        // Navigate to Admin Login screen
        btnAdmin.setOnClickListener {
            startActivity(Intent(this, AdminLoginActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        // Navigate to Sign Up screen
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)
        tvSignUp?.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        // Sign Up
        findViewById<TextView>(R.id.tvSignUp).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        // Forgot password
        tvForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    private fun validate(): Boolean {
        tilCashierId.error = null
        tilPassword.error  = null
        tvError.visibility = View.GONE
        var ok = true
        if (etCashierId.text.toString().trim().isEmpty()) {
            tilCashierId.error = "Cashier ID is required"; ok = false
        }
        if (etPassword.text.toString().trim().isEmpty()) {
            tilPassword.error = "Password is required"; ok = false
        }
        return ok
    }

    private fun performLogin(isAdmin: Boolean) {
        setLoading(true)

        val id = etCashierId.text.toString().trim()
        val pw = etPassword.text.toString().trim()

        // Simulate brief auth delay for UX
        Handler(Looper.getMainLooper()).postDelayed({
            val emp = db.getEmployee(id)

            when {
                emp == null -> {
                    setLoading(false)
                    tilCashierId.error = "Cashier ID not found"
                }
                emp.password != pw -> {
                    setLoading(false)
                    tilPassword.error = "Incorrect password"
                    etPassword.text?.clear()
                }
                isAdmin && emp.role != "admin" -> {
                    setLoading(false)
                    showError("This account does not have admin privileges.")
                }
                else -> {
                    // Save session
                    getSharedPreferences("basteakoy_prefs", MODE_PRIVATE).edit()
                        .putBoolean("is_logged_in", true)
                        .putString("cashier_id",   emp.cashierId)
                        .putString("emp_name",      emp.name)
                        .putString("emp_role",      emp.role)
                        .putString("emp_email",     emp.email)
                        .putString("emp_phone",     emp.phone)
                        .putString("emp_shift",     emp.shift)
                        .apply()
                    goToMain()
                }
            }
        }, 800)
    }

    private fun setLoading(loading: Boolean) {
        progress.visibility  = if (loading) View.VISIBLE else View.GONE
        btnLogin.isEnabled   = !loading
        btnAdmin.isEnabled   = !loading
        etCashierId.isEnabled = !loading
        etPassword.isEnabled  = !loading
    }

    private fun showError(msg: String) {
        tvError.text = msg
        tvError.visibility = View.VISIBLE
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
