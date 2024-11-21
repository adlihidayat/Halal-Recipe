package com.example.halalrecipe.activity

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.halalrecipe.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.common.api.ApiException
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.OAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.email_edit_text)
        val passwordEditText = findViewById<EditText>(R.id.password_edit_text)

        // Control email hint

        emailEditText.hint = getString(R.string.login_email_hint)
        emailEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            emailEditText.hint = if (hasFocus) "" else getString(R.string.login_email_hint)
        }

        // Control password hint
        passwordEditText.hint = getString(R.string.login_password_hint)
        passwordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            passwordEditText.hint = if (hasFocus) "" else getString(R.string.login_password_hint)
        }

        val loginButton = findViewById<Button>(R.id.login_button) // Make sure you have this button in your layout
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Check for empty fields
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            } else {
                // Sign in the user manually
                signInWithEmailPassword(email, password)
            }
        }

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set up Google sign-in button
        val googleSignInButton = findViewById<LinearLayout>(R.id.googleSignInButton)
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        val githubSignInButton = findViewById<LinearLayout>(R.id.githubSignInButton)
        githubSignInButton.setOnClickListener {
            signInWithGithub()
        }




        // Checkbox listener
        val checkBox = findViewById<CheckBox>(R.id.my_checkbox)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) "Checkbox is checked!" else "Checkbox is unchecked!"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        // Navigate to RegisterActivity
        val registerButton = findViewById<Button>(R.id.register_button)
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
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

    private fun signInWithEmailPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful, proceed to next screen
                    val user = auth.currentUser
                    Toast.makeText(this, "Welcome ${user?.email}", Toast.LENGTH_SHORT).show()

                    // Navigate to RegisterActivity or home screen
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Login failed
                    val exception = task.exception
                    Log.e("LoginActivity", "Login failed", exception)
                    Toast.makeText(this, "Authentication failed: ${exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
