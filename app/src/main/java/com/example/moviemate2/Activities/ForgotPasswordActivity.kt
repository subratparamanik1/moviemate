package com.example.moviemate2.Activities


import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moviemate2.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var edEmailLayout: TextInputLayout
    private lateinit var edEmail: TextInputEditText
    private lateinit var resetPasswordBtn: MaterialButton
    private lateinit var textViewBackToLogin: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpass)

        //Variable initialization
        auth = FirebaseAuth.getInstance()
        edEmailLayout = findViewById(R.id.edEmailLayout)
        edEmail = findViewById(R.id.edEmail)
        resetPasswordBtn = findViewById(R.id.resetPasswordBtn)
        textViewBackToLogin = findViewById(R.id.textViewBackToLogin)

        resetPasswordBtn.setOnClickListener {
            resetPassword()
        }


        textViewBackToLogin.setOnClickListener {
            finish()
        }
    }

    private fun resetPassword() {
        val email = edEmail.text.toString().trim()

        if (email.isEmpty()) {
            edEmailLayout.error = "Email is required"
            return
        }

        // Send password reset email
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Reset link sent to your email!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
