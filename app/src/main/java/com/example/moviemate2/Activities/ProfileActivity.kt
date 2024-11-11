package com.example.moviemate2.Activities

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.moviemate2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var btnEditProfile: Button
    private lateinit var btnChangePassword: Button
    private lateinit var btnLogout: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Initialize UI elements
        profileImage = findViewById(R.id.profile_image)
        profileName = findViewById(R.id.profile_name)
        profileEmail = findViewById(R.id.profile_email)
        btnEditProfile = findViewById(R.id.btn_edit_profile)
        btnChangePassword = findViewById(R.id.btn_change_pass)
        btnLogout = findViewById(R.id.btn_logout)
        progressBar = findViewById(R.id.progress_bar)

        val currentUser = auth.currentUser
        val uid = currentUser?.uid ?: ""

        if (uid.isNotEmpty() && isNetworkAvailable()) {
            // Show progress bar while loading data
            progressBar.visibility = View.VISIBLE

            progressBar.visibility = android.view.View.VISIBLE

            // Fetch user data from Firebase using coroutines
            lifecycleScope.launch {
                try {
                    val snapshot = database.getReference("users").child(uid).get().await()
                    if (snapshot.exists()) {
                        val userName = snapshot.child("userName").getValue(String::class.java) ?: ""
                        val userEmail = snapshot.child("emailId").getValue(String::class.java) ?: ""

                        // Set profile information
                        profileName.text = userName
                        profileEmail.text = userEmail
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@ProfileActivity, "Failed to load profile data", Toast.LENGTH_SHORT).show()
                } finally {
                    progressBar.visibility = android.view.View.GONE
                }
            }
        } else {
            Toast.makeText(this, "No network connection", Toast.LENGTH_SHORT).show()
        }

        // Set up onClickListeners
        btnEditProfile.setOnClickListener {
            // Open Edit Profile screen
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        btnChangePassword.setOnClickListener {
            // Open Forgot Password screen
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            // Handle logout functionality with coroutine to avoid UI blocking
            lifecycleScope.launch {
                auth.signOut()
                Toast.makeText(this@ProfileActivity, "Logged Out", Toast.LENGTH_SHORT).show()

                // Redirect to login screen after logging out
                val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    // Function to check network availability
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}
