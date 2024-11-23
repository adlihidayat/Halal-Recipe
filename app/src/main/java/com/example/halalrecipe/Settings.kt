package com.example.halalrecipe

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.halalrecipe.activity.LoginActivity
import com.example.halalrecipe.activity.NotificationsActivity
import com.google.firebase.auth.FirebaseAuth

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Settings : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Find the LinearLayout for logout
        val logoutLayout: LinearLayout = view.findViewById(R.id.linearLayout_logout)

        // Set click listener for the LinearLayout
        logoutLayout.setOnClickListener {
            // Sign out from Firebase
            auth.signOut()

            // Redirect to the login screen
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish() // Close the current fragment
        }

        // Find the LinearLayout for navigating to NotificationsActivity
        val notificationsLayout: LinearLayout = view.findViewById(R.id.linearLayout_notifications)

        // Set click listener for navigating to NotificationsActivity
        notificationsLayout.setOnClickListener {
            // Navigate to NotificationsActivity
            val intent = Intent(activity, NotificationsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish() // Close the current fragment
        }

        val accountInformationButton: LinearLayout = view.findViewById(R.id.account_information_button)
        accountInformationButton.setOnClickListener {
            try {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, FragmentAccountInformation())
                    .addToBackStack(null)
                    .commit()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }



        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Settings.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Settings().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
