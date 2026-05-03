package com.example.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home    -> { replaceFragment(HomeFragment());       true }
                R.id.bottom_order   -> { replaceFragment(OrderFragment());      true }
                R.id.bottom_add     -> { replaceFragment(AddProductFragment()); true }
                R.id.bottom_sales   -> { replaceFragment(SalesFragment());      true }
                R.id.bottom_profile -> { replaceFragment(ProfileFragment());    true }
                else -> false
            }
        }

        // Default: Home
        replaceFragment(HomeFragment())
        bottomNav.selectedItemId = R.id.bottom_home
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .commit()
    }
}
