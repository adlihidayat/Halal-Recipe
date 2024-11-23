package com.example.halalrecipe.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.halalrecipe.R

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        // Referensi ImageView tombol back
        val tombolBack: ImageView = findViewById(R.id.tombolback)

        // Klik listener untuk tombol back
        tombolBack.setOnClickListener {
            // Navigasi ke HomeActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val btnWhatsapp: LinearLayout = findViewById(R.id.btnWhatsapp)

        btnWhatsapp.setOnClickListener {
            openUrl("https://wa.link/9jsuan")
        }

        val tombolsrs: LinearLayout = findViewById(R.id.tombolsrs)

        tombolsrs.setOnClickListener {
            openUrl("https://docs.google.com/document/d/1GZUKzRUKWWp9N-CCLAcZOxt5OsP8t-MbmiEbAwCSFvw/edit")
        }

        val tombolinfo: LinearLayout = findViewById(R.id.tombolinfo)

        tombolinfo.setOnClickListener {
            val intent = Intent(this, AppinfoActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun openUrl(link: String) {
        val uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Tidak ada aplikasi untuk membuka URL ini.", Toast.LENGTH_SHORT).show()
        }
    }
}