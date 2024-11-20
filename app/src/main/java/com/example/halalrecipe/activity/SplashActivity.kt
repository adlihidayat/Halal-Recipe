package com.example.halalrecipe.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.halalrecipe.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Set a delay of 3 seconds before navigating to LoginActivity
        Handler().postDelayed({
            // Navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Close SplashActivity so the user can't navigate back to it
        }, 3000) // 3000 milliseconds = 3 seconds
    }
}
