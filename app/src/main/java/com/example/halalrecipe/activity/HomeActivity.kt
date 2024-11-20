package com.example.halalrecipe.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.halalrecipe.Home
import com.example.halalrecipe.R
import com.example.halalrecipe.Saved
import com.example.halalrecipe.Search
import com.example.halalrecipe.Settings
import com.example.halalrecipe.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding : ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Home())

        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.home -> replaceFragment(Home())
                R.id.search -> replaceFragment(Search())
                R.id.saved -> replaceFragment(Saved())
                R.id.settings -> replaceFragment(Settings())

                else ->{

                }
            }
            true
        }

        auth = FirebaseAuth.getInstance()
    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()


    }
}