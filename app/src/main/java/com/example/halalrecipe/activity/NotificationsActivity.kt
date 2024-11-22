package com.example.halalrecipe.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.halalrecipe.R

class NotificationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        // Referensi ImageView tombol back
        val tombolBack: ImageView = findViewById(R.id.tombolback)

        // Klik listener untuk tombol back
        tombolBack.setOnClickListener {
            // Navigasi ke HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Temukan Switch dari layout
        val switchToggle: Switch = findViewById(R.id.switch_toggle)

        // Tambahkan listener untuk menangani perubahan status
        switchToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Logika jika switch diaktifkan
                Toast.makeText(this, "Notifikasi diaktifkan", Toast.LENGTH_SHORT).show()
            } else {
                // Logika jika switch dimatikan
                Toast.makeText(this, "Notifikasi dimatikan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
