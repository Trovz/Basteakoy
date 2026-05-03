package com.example.fragment

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logo     = findViewById<ImageView>(R.id.splashLogo)
         val progress = findViewById<ProgressBar>(R.id.splashProgress)

        // Fade-in animation
        logo.animate().alpha(1f).setDuration(700).setStartDelay(100).start()
         progress.animate().alpha(1f).setDuration(400).setStartDelay(900).start()

        lifecycleScope.launch {
            delay(2600)
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}
