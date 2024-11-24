package com.example.halalrecipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class FragmentAppinfo : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_appinfo, container, false)

        val tombolback: Button = view.findViewById(R.id.tombolback)

        tombolback.setOnClickListener {
            parentFragmentManager.popBackStack() // Navigate back to the previous fragment
        }
        return view
    }
    }