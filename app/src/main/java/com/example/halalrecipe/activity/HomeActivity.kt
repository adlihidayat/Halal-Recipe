package com.example.halalrecipe.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.halalrecipe.R
import com.google.firebase.auth.FirebaseAuth

class HomeActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        val logoutButton: Button = findViewById(R.id.logoutButton)

        // Set click listener on the logout button
        logoutButton.setOnClickListener {
            // Sign out from Firebase
            auth.signOut()

            // Redirect to the login screen or perform any additional actions
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Close the current activity
        }
    }
}