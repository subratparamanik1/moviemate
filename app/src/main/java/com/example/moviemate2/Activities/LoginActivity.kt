package com.example.moviemate2.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moviemate2.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userEdt: EditText
    private lateinit var passEdt: EditText
    private lateinit var loginBtn: Button
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var registerNowTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        registerNowTextView = findViewById(R.id.textView4)
        registerNowTextView.setOnClickListener {

            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()
        userEdt = findViewById(R.id.editTextText)
        passEdt = findViewById(R.id.editTextPassword)
        loginBtn = findViewById(R.id.loginBtn)
        forgotPasswordTextView = findViewById(R.id.textView2)
        registerNowTextView = findViewById(R.id.textView4)

        loginBtn.setOnClickListener {
            handleLogin()
        }

        forgotPasswordTextView.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity ::class.java))
        }

        registerNowTextView.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
    //function to handle login
    private fun handleLogin() {
        val emailId = userEdt.text.toString().trim()
        val password = passEdt.text.toString().trim()

        if (emailId.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in your username and password", Toast.LENGTH_SHORT)
                .show()
            return
        }
        auth.signInWithEmailAndPassword(emailId, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Your username or password is incorrect",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}