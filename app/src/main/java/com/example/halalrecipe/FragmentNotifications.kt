package com.example.halalrecipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.auth.FirebaseAuth

class FragmentNotifications : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        // Find the back button and set its click listener
        val tombolback: ImageView = view.findViewById(R.id.tombolback)
        tombolback.setOnClickListener {
            parentFragmentManager.popBackStack() // Navigate back to the previous fragment
        }
        // Temukan Switch dari layout
        val switchToggle: SwitchCompat = view.findViewById(R.id.switch_toggle)

        // Tambahkan listener untuk menangani perubahan status
        switchToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Logika jika switch diaktifkan
                Toast.makeText(requireContext(), "Notifikasi diaktifkan", Toast.LENGTH_SHORT).show()
            } else {
                // Logika jika switch dimatikan
                Toast.makeText(requireContext(), "Notifikasi dimatikan", Toast.LENGTH_SHORT).show()
            }
        }

        // Temukan Switch dari layout
        val switchToggle1: SwitchCompat = view.findViewById(R.id.switch_toggle1)

        // Tambahkan listener untuk menangani perubahan status
        switchToggle1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Logika jika switch diaktifkan
                Toast.makeText(requireContext(), "Notifikasi Email diaktifkan", Toast.LENGTH_SHORT).show()
            } else {
                // Logika jika switch dimatikan
                Toast.makeText(requireContext(), "Notifikasi Email dimatikan", Toast.LENGTH_SHORT).show()
            }
        }

        // Temukan Switch dari layout
        val switchToggle2: SwitchCompat = view.findViewById(R.id.switch_toggle2)

        // Tambahkan listener untuk menangani perubahan status
        switchToggle2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Logika jika switch diaktifkan
                Toast.makeText(requireContext(), "Notifikasi SMS diaktifkan", Toast.LENGTH_SHORT).show()
            } else {
                // Logika jika switch dimatikan
                Toast.makeText(requireContext(), "Notifikasi SMS dimatikan", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}