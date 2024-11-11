package com.example.moviemate2.Activities

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moviemate2.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.moviemate2.models.User  // Ensure the correct import

class EditProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var edUserName: TextInputEditText
    private lateinit var edEmailId: TextInputEditText
    private lateinit var edPhoneNum: TextInputEditText
    private lateinit var edUserNameLayout: TextInputLayout
    private lateinit var edEmailIdLayout: TextInputLayout
    private lateinit var edPhoneNumLayout: TextInputLayout
    private lateinit var updateBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        // Initialize views
        edUserName = findViewById(R.id.edUserName)
        edEmailId = findViewById(R.id.edEmailId)
        edPhoneNum = findViewById(R.id.edPhoneNum)
        edUserNameLayout = findViewById(R.id.edUserNameLayout)
        edEmailIdLayout = findViewById(R.id.edEmailIdLayout)
        edPhoneNumLayout = findViewById(R.id.edPhoneNumLayout)
        updateBtn = findViewById(R.id.updateBtn)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Get the current user UID
        val uid = auth.currentUser?.uid

        if (uid != null) {
            // Initialize Firebase Database reference
            database = FirebaseDatabase.getInstance().getReference("users").child(uid)
        } else {
            // Handle the case when the user is not logged in
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish() // Optionally finish the activity
            return
        }

        loadUserData()

        // Update profile on button click
        updateBtn.setOnClickListener {
            updateUserData()
        }
    }

    // Function to load user data from Firebase
    private fun loadUserData() {
        // Ensure the userId is not null
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Retrieve the user data and check for null values
                    val userData = snapshot.getValue(User::class.java)
                    if (userData != null) {
                        edUserName.setText(userData.userName)
                        edEmailId.setText(userData.emailId)
                        edPhoneNum.setText(userData.phoneNum)
                    } else {
                        Toast.makeText(this@EditProfileActivity, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@EditProfileActivity, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    // Function to update user data in Firebase
    private fun updateUserData() {
        // Get updated values from the input fields
        val newUserName = edUserName.text.toString().trim()
        val newEmailId = edEmailId.text.toString().trim()
        val newPhoneNum = edPhoneNum.text.toString().trim()

        // Simple validation checks
        if (newUserName.isEmpty()) {
            edUserNameLayout.error = "Name is required"
            return
        }
        if (newEmailId.isEmpty()) {
            edEmailIdLayout.error = "Email is required"
            return
        }
        if (newPhoneNum.isEmpty()) {
            edPhoneNumLayout.error = "Phone number is required"
            return
        }

        // Clear error messages
        edUserNameLayout.error = null
        edEmailIdLayout.error = null
        edPhoneNumLayout.error = null

        // Create a map to update the fields
        val userMap = mapOf(
            "userName" to newUserName,
            "emailId" to newEmailId,
            "phoneNum" to newPhoneNum
        )

        // Update the data in Firebase
        database.updateChildren(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
