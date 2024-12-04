package com.example.halalrecipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth

class Settings : Fragment() {

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
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the LinearLayout for logout
        val logoutLayout: LinearLayout = view.findViewById(R.id.linearLayout_logout)
        logoutLayout.setOnClickListener {
            val showPopUp = PopUpFragment()
            showPopUp.show((activity as AppCompatActivity).supportFragmentManager, "showPopUp")
            showPopUp.setCancelable(false)
        }

        // Find the LinearLayout for navigating to NotificationsFragment
        val favoriteLayout: LinearLayout = view.findViewById(R.id.tombolfavorite)
        favoriteLayout.setOnClickListener {
            // Replace the current fragment with NotificationsFragment
            val fragmentTransaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, Saved())
            fragmentTransaction.addToBackStack(null) // Add to back stack so you can go back
            fragmentTransaction.commit() // Close the current fragment
        }

        // Find the LinearLayout for navigating to NotificationsFragment
        val notificationsLayout: LinearLayout = view.findViewById(R.id.linearLayout_notifications)
        notificationsLayout.setOnClickListener {
            // Replace the current fragment with NotificationsFragment
            val fragmentTransaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, FragmentNotifications())
            fragmentTransaction.addToBackStack(null) // Add to back stack so you can go back
            fragmentTransaction.commit() // Close the current fragment
        }

        // Find the LinearLayout for navigating to HelpFragment
        val helpLayout: LinearLayout = view.findViewById(R.id.linearLayout_help)
        helpLayout.setOnClickListener {
            // Intent to switch to HelpActivity
            val fragmentTransaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, FragmentHelp())
            fragmentTransaction.addToBackStack(null) // Add to back stack so you can go back
            fragmentTransaction.commit() // Close the current fragment
        }

        // Find the LinearLayout for "Account Information"
        val accountInformationButton: LinearLayout = view.findViewById(R.id.account_information_button)

        // Set click listener for the "Account Information" button
        accountInformationButton.setOnClickListener {
            // Replace the current fragment with FragmentAccountInformation
            val fragmentTransaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, FragmentAccountInformation())
            fragmentTransaction.addToBackStack(null) // Add to back stack so you can go back
            fragmentTransaction.commit()
        }
    }
}
