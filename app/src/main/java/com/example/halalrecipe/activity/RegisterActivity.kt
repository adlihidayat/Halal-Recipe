package com.example.halalrecipe.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.halalrecipe.R
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore


class RegisterActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val fullnameEditText = findViewById<EditText>(R.id.fullname_edit_text)
        val emailEditText = findViewById<EditText>(R.id.email_edit_text)
        val passwordEditText = findViewById<EditText>(R.id.password_edit_text)
        val reenterPasswordEditText = findViewById<EditText>(R.id.reenter_password_edit_text)
        val registerToLoginButton = findViewById<Button>(R.id.login_button)
        val registerButton = findViewById<Button>(R.id.register_button)

        fullnameEditText.hint = getString(R.string.register_fullname_hint)
        fullnameEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            fullnameEditText.hint = if (hasFocus) "" else getString(R.string.register_fullname_hint)
        }

        emailEditText.hint = getString(R.string.register_email_hint)
        emailEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            emailEditText.hint = if (hasFocus) "" else getString(R.string.register_email_hint)
        }

        passwordEditText.hint = getString(R.string.register_password_hint)
        passwordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            passwordEditText.hint = if (hasFocus) "" else getString(R.string.register_password_hint)
        }

        reenterPasswordEditText.hint = getString(R.string.register_reenter_password_hint)
        reenterPasswordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            reenterPasswordEditText.hint = if (hasFocus) "" else getString(R.string.register_reenter_password_hint)
        }

        registerToLoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Set up Google sign-in button
        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        val googleSignInButton = findViewById<LinearLayout>(R.id.googleSignInButton)
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        val githubSignInButton = findViewById<LinearLayout>(R.id.githubSignInButton)
        githubSignInButton.setOnClickListener {
            signInWithGithub()
        }

        registerButton.setOnClickListener {
            val fullName = fullnameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val reenterPassword = reenterPasswordEditText.text.toString().trim()

            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && reenterPassword.isNotEmpty()) {
                registerUser(fullName, email, password, reenterPassword)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun registerUser(fullName: String, email: String, password: String, reenterPassword: String) {
        // Check if the passwords match
        if (password != reenterPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if the password is at least 8 characters long
        if (password.length < 8) {
            Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if the password contains at least one number
        if (!password.any { it.isDigit() }) {
            Toast.makeText(this, "Password must contain at least one number", Toast.LENGTH_SHORT).show()
            return
        }

        // If all checks pass, proceed with registration
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid
                    if (userId != null) {
                        // Create a user map with manual registration data
                        val userMap = hashMapOf(
                            "name" to fullName,
                            "email" to email,
                            "profile" to "", // Empty profile picture
                            "provider" to "manual" // Indicates manual registration
                        )

                        firestore.collection("account").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                finish() // Finish the activity and return to the login screen
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    val exception = task.exception
                    Toast.makeText(this, "Registration failed: ${exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e("LoginActivity", "Google Sign-In failed", e)
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()

                    // Store user data in Firestore under 'account'
                    val userId = user?.uid
                    if (userId != null) {
                        val userData = hashMapOf(
                            "name" to user.displayName,
                            "email" to user.email,
                            "profile" to (user.photoUrl?.toString() ?: ""),
                            "provider" to "google"  // Add the provider field for tracking purposes
                        )

                        // Save the user data in Firestore under the 'account' collection
                        firestore.collection("account").document(userId).set(userData)
                            .addOnSuccessListener {
                                Log.d("LoginActivity", "User data saved successfully")
                                // Navigate to RegisterActivity upon successful login
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish() // Optional: close LoginActivity if you don't want to return to it
                            }
                            .addOnFailureListener { e ->
                                Log.e("LoginActivity", "Failed to save user data", e)
                                Toast.makeText(this, "Failed to save user data: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Log.e("LoginActivity", "User ID is null")
                    }
                } else {
                    val exception = task.exception
                    Log.e("LoginActivity", "Authentication Failed", exception)
                    Toast.makeText(this, "Authentication Failed: ${exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInWithGithub() {
        // Create an OAuthProvider for GitHub
        val provider = OAuthProvider.newBuilder("github.com")
        val auth = FirebaseAuth.getInstance()

        // Check if there's a pending result
        auth.pendingAuthResult?.let {
            it.addOnSuccessListener { authResult ->
                // Handle successful sign-in
                onGithubSignInSuccess(authResult)
            }.addOnFailureListener { e ->
                Log.e("LoginActivity", "Error with GitHub Sign-In", e)
                Toast.makeText(this, "GitHub Sign-In failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            // Start GitHub sign-in process
            auth.startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener { authResult ->
                    onGithubSignInSuccess(authResult)
                }
                .addOnFailureListener { e ->
                    Log.e("LoginActivity", "Error with GitHub Sign-In", e)
                    Toast.makeText(this, "GitHub Sign-In failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun onGithubSignInSuccess(authResult: AuthResult) {
        val user = authResult.user
        val firestore = FirebaseFirestore.getInstance()

        // Save user data under 'account' collection
        val userId = user?.uid
        if (userId != null) {
            val userData = hashMapOf(
                "name" to (user.displayName ?: ""),
                "email" to (user.email ?: ""),
                "profile" to (user.photoUrl?.toString() ?: ""),
                "provider" to "github"
            )

            // Save user data to Firestore
            firestore.collection("account").document(userId).set(userData)
                .addOnSuccessListener {
                    Log.d("LoginActivity", "GitHub user data saved successfully")
                    // Navigate to next activity or update UI as needed
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("LoginActivity", "Failed to save GitHub user data", e)
                    Toast.makeText(this, "Failed to save GitHub user data: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.e("LoginActivity", "User ID is null")
        }
    }

}
