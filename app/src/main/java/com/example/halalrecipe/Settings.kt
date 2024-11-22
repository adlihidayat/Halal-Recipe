package com.example.halalrecipe

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.halalrecipe.activity.HelpActivity
import com.example.halalrecipe.activity.LoginActivity
import com.example.halalrecipe.activity.NotificationsActivity
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

        // Find the LinearLayout for navigating to NotificationsActivity
        val notificationsLayout: LinearLayout = view.findViewById(R.id.linearLayout_notifications)
        notificationsLayout.setOnClickListener {
            // Navigate to NotificationsActivity
            val intent = Intent(activity, NotificationsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish() // Close the current fragment
        }

        // Find the LinearLayout for navigating to HelpActivity
        val helpLayout: LinearLayout = view.findViewById(R.id.linearLayout_help)
        helpLayout.setOnClickListener {
            // Intent to switch to HelpActivity
            val intent = Intent(activity, HelpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
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
