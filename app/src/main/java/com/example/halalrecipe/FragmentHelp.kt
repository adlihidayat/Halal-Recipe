package com.example.halalrecipe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth


class FragmentHelp : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_help, container, false)

        // Find the back button and set its click listener
        val tombolback= view.findViewById<ImageView>(R.id.tombolback)
        tombolback.setOnClickListener {
            parentFragmentManager.popBackStack() // Navigate back to the previous fragment
        }

        val tombolhelpcenter = view.findViewById<LinearLayout>(R.id.tombolhelpcenter)

        tombolhelpcenter.setOnClickListener {
            openUrl("https://wa.link/9jsuan")
        }

        val tombolsrs: LinearLayout = view.findViewById(R.id.tombolsrs)

        tombolsrs.setOnClickListener {
            openUrl("https://docs.google.com/document/d/1GZUKzRUKWWp9N-CCLAcZOxt5OsP8t-MbmiEbAwCSFvw/edit")
        }

        val tombolinfo: LinearLayout = view.findViewById(R.id.tombolinfo)

        tombolinfo.setOnClickListener {
            val fragmentTransaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, FragmentAppinfo())
            fragmentTransaction.addToBackStack(null) // Add to back stack so you can go back
            fragmentTransaction.commit()
        }
        return view

    }

    private fun openUrl(link: String) {
        val uri = Uri.parse(link)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Tidak ada aplikasi untuk membuka URL ini.", Toast.LENGTH_SHORT).show()
        }
    }
}