package com.example.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SignUpActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        db = DatabaseHelper(this)

        val btnBack     = findViewById<ImageButton>(R.id.btnSignUpBack)
        val tilName     = findViewById<TextInputLayout>(R.id.tilSignUpName)
        val tilId       = findViewById<TextInputLayout>(R.id.tilSignUpId)
        val tilEmail    = findViewById<TextInputLayout>(R.id.tilSignUpEmail)
        val tilPhone    = findViewById<TextInputLayout>(R.id.tilSignUpPhone)
        val tilPw       = findViewById<TextInputLayout>(R.id.tilSignUpPassword)
        val tilConfirm  = findViewById<TextInputLayout>(R.id.tilSignUpConfirmPassword)
        val etName      = findViewById<TextInputEditText>(R.id.etSignUpName)
        val etId        = findViewById<TextInputEditText>(R.id.etSignUpId)
        val etEmail     = findViewById<TextInputEditText>(R.id.etSignUpEmail)
        val etPhone     = findViewById<TextInputEditText>(R.id.etSignUpPhone)
        val etPw        = findViewById<TextInputEditText>(R.id.etSignUpPassword)
        val etConfirm   = findViewById<TextInputEditText>(R.id.etSignUpConfirmPassword)
        val spinnerShift= findViewById<Spinner>(R.id.spinnerShift)
        val btnSignUp   = findViewById<Button>(R.id.btnSignUp)
        val tvError     = findViewById<TextView>(R.id.tvSignUpError)
        val progress    = findViewById<ProgressBar>(R.id.signUpProgress)
        val tvBackLogin = findViewById<TextView>(R.id.tvBackToLogin)

        // Shift options
        val shifts = listOf(
            "Morning Shift (6AM - 2PM)",
            "Afternoon Shift (2PM - 10PM)",
            "Night Shift (10PM - 6AM)"
        )
        spinnerShift.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, shifts).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        btnBack.setOnClickListener { finish() }
        tvBackLogin.setOnClickListener { finish() }

        // Clear errors on focus
        listOf(etName, etId, etEmail, etPhone, etPw, etConfirm).forEach { et ->
            et.setOnFocusChangeListener { _, _ ->
                tvError.visibility = View.GONE
            }
        }

        btnSignUp.setOnClickListener {
            clearErrors(tilName, tilId, tilEmail, tilPw, tilConfirm)
            tvError.visibility = View.GONE

            val name    = etName.text.toString().trim()
            val id      = etId.text.toString().trim()
            val email   = etEmail.text.toString().trim()
            val phone   = etPhone.text.toString().trim().ifEmpty { "—" }
            val shift   = spinnerShift.selectedItem.toString()
            val pw      = etPw.text.toString()
            val confirm = etConfirm.text.toString()

            // Validation
            var valid = true
            if (name.isEmpty())  { tilName.error = "Full name is required"; valid = false }
            if (id.isEmpty())    { tilId.error   = "Cashier ID is required"; valid = false }
            else if (id.length < 4) { tilId.error = "ID must be at least 4 digits"; valid = false }
            if (email.isEmpty()) { tilEmail.error = "Email is required"; valid = false }
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = "Enter a valid email"; valid = false
            }
            if (pw.isEmpty())    { tilPw.error = "Password is required"; valid = false }
            else if (pw.length < 6) { tilPw.error = "Minimum 6 characters"; valid = false }
            if (confirm.isEmpty()) { tilConfirm.error = "Please confirm your password"; valid = false }
            else if (pw != confirm) { tilConfirm.error = "Passwords do not match"; valid = false }

            if (!valid) return@setOnClickListener

            setLoading(true, btnSignUp, progress, etName, etId, etEmail, etPhone, etPw, etConfirm)

            Handler(Looper.getMainLooper()).postDelayed({
                val result = db.registerEmployee(id, pw, name, email, phone, shift)
                setLoading(false, btnSignUp, progress, etName, etId, etEmail, etPhone, etPw, etConfirm)

                when (result) {
                    DatabaseHelper.RegisterResult.SUCCESS -> {
                        Toast.makeText(this,
                            "✅ Account created! Welcome, $name!", Toast.LENGTH_LONG).show()
                        // Auto-login
                        getSharedPreferences("basteakoy_prefs", MODE_PRIVATE).edit()
                            .putBoolean("is_logged_in", true)
                            .putString("cashier_id", id)
                            .putString("emp_name", name)
                            .putString("emp_role", "cashier")
                            .putString("emp_email", email)
                            .putString("emp_phone", phone)
                            .putString("emp_shift", shift)
                            .apply()
                        val intent = Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                    }
                    DatabaseHelper.RegisterResult.ID_EXISTS ->
                        showError(tvError, "❌ Cashier ID '$id' is already taken. Choose a different ID.")
                    DatabaseHelper.RegisterResult.INVALID_ID ->
                        showError(tvError, "❌ Cashier ID must be at least 4 digits.")
                    DatabaseHelper.RegisterResult.WEAK_PASSWORD ->
                        showError(tvError, "❌ Password is too short. Minimum 6 characters.")
                    else ->
                        showError(tvError, "❌ Registration failed. Please try again.")
                }
            }, 900)
        }
    }

    private fun clearErrors(vararg tils: TextInputLayout) = tils.forEach { it.error = null }

    private fun showError(tv: TextView, msg: String) {
        tv.text = msg; tv.visibility = View.VISIBLE
    }

    private fun setLoading(loading: Boolean, btn: Button, pb: ProgressBar,
                           vararg fields: TextInputEditText) {
        pb.visibility = if (loading) View.VISIBLE else View.GONE
        btn.isEnabled = !loading
        fields.forEach { it.isEnabled = !loading }
    }
}
