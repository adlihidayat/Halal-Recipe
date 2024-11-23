package com.example.halalrecipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class FragmentAccountInformation : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_account_information, container, false)

        // Find the back button and set its click listener
        val backButton = view.findViewById<ImageButton>(R.id.homeButton)
        backButton?.setOnClickListener {
            parentFragmentManager.popBackStack() // Navigate back to the previous fragment
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = FragmentAccountInformation()
    }
}
