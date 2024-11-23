package com.example.halalrecipe

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.halalrecipe.activity.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FragmentAccountInformation : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailTextView: TextView
    private lateinit var fullNameEditText: EditText
    private lateinit var profilePictureEditText: EditText
    private lateinit var deleteButton: Button
    private lateinit var saveButton: Button

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

        emailTextView = view.findViewById(R.id.emailEditText)
        fullNameEditText = view.findViewById(R.id.fullNameEditText)
        profilePictureEditText = view.findViewById(R.id.profilePicture)
        deleteButton = view.findViewById(R.id.deleteButton)
        saveButton = view.findViewById(R.id.saveButton)

        loadUserData()

        // Find the back button and set its click listener
        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        backButton?.setOnClickListener {
            parentFragmentManager.popBackStack() // Navigate back to the previous fragment
        }

        // Handle Delete Button Click
        deleteButton.setOnClickListener {
            showCustomDeleteDialog() // Show the custom popup
        }

        // Handle Save Button Click
        saveButton.setOnClickListener {
            val updatedData = hashMapOf<String, Any>(
                "name" to fullNameEditText.text.toString(),
                "profile" to profilePictureEditText.text.toString()
            )

            // Update the Firestore document
            val firestore = FirebaseFirestore.getInstance()
            val currentUser = auth.currentUser

            if (currentUser != null) {
                val userId = currentUser.uid

                firestore.collection("account")
                    .document(userId)
                    .update(updatedData as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Account updated successfully.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to update account: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "No user is logged in.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid // Get the user's UID

            // Use Firestore to fetch data from the 'account' collection
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("account")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Get fields from Firestore document
                        val email = document.getString("email")
                        val name = document.getString("name")
                        val profile = document.getString("profile")

                        // Populate the fields in the UI
                        emailTextView.text = email ?: "N/A"
                        fullNameEditText.setText(name ?: "")
                        profilePictureEditText.setText(profile ?: "")
                    } else {
                        Log.e("FirestoreError", "Document does not exist.")
                        Toast.makeText(context, "User data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreError", "Error fetching user data: ${e.message}")
                    Toast.makeText(context, "Failed to load user data.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "User is not logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCustomDeleteDialog() {
        // Inflate the custom layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_delete_account, null)

        // Create the AlertDialog
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme) // Apply custom theme
            .setView(dialogView)
            .create()

        // Find the buttons in the custom layout
        val cancelButton = dialogView.findViewById<Button>(R.id.btn_cancel)
        val deleteButton = dialogView.findViewById<Button>(R.id.btn_confirm)

        // Set button click listeners
        cancelButton.setOnClickListener {
            // Close the dialog
            dialog.dismiss()
        }

        deleteButton.setOnClickListener {
            // Perform delete action
            deleteAccount()
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }



    private fun deleteAccount() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Delete the user's data from Firestore
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("account")
                .document(userId)
                .delete()
                .addOnSuccessListener {
                    Log.d("AccountDeletion", "User account deleted successfully from Firestore.")
                    Toast.makeText(context, "Account deleted successfully.", Toast.LENGTH_SHORT).show()

                    // Delete the user's authentication account
                    currentUser.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("AuthDeletion", "User authentication account deleted.")

                                // Redirect to LoginActivity
                                val intent = android.content.Intent(requireContext(), LoginActivity::class.java)
                                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                Log.e("AuthDeletionError", "Failed to delete auth account: ${task.exception?.message}")
                                Toast.makeText(context, "Failed to delete authentication account.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreError", "Error deleting user account: ${e.message}")
                    Toast.makeText(context, "Failed to delete account from Firestore.", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "No user is logged in.", Toast.LENGTH_SHORT).show()
        }
    }

}
