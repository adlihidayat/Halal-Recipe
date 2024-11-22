package com.example.halalrecipe.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.halalrecipe.Home
import com.example.halalrecipe.R
import com.example.halalrecipe.Saved
import com.example.halalrecipe.Search
import com.example.halalrecipe.Settings
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Load HomeFragment by default when the activity starts
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Home()) // Add HomeFragment into the container
                .commit()
        }

        // Set listener to handle navigation item selections
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> replaceFragment(Home())
                R.id.saved -> replaceFragment(Saved())
                R.id.search -> replaceFragment(Search())
                R.id.settings -> replaceFragment(Settings())
                else -> false
            }
            true
        }
    }

    // Helper method to replace fragments in the FrameLayout container
    private fun replaceFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return true
    }
}
