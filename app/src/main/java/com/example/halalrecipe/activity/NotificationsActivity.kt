package com.example.halalrecipe.activity

import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.halalrecipe.R

class NotificationsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        // Temukan Switch dari layout
        val switchToggle: Switch = findViewById(R.id.switch_toggle)

        // Tambahkan listener untuk menangani perubahan status
        switchToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Logika jika switch diaktifkan
                Toast.makeText(this, "Switch diaktifkan", Toast.LENGTH_SHORT).show()
            } else {
                // Logika jika switch dimatikan
                Toast.makeText(this, "Switch dimatikan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
