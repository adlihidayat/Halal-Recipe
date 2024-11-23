package com.example.halalrecipe.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.halalrecipe.R

class AppinfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appinfo)

        // Referensi ImageView tombol back
        val tombolBack: Button = findViewById(R.id.tombolback)

        // Klik listener untuk tombol back
        tombolBack.setOnClickListener {
            // Navigasi ke HomeActivity
            val intent = Intent(this, HelpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}