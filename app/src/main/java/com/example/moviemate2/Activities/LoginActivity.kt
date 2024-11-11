package com.example.moviemate2.Activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moviemate2.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var edEmailId: EditText
    private lateinit var edEmailIdLayout: TextInputLayout
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
        edEmailId = findViewById(R.id.editTextText)
        edEmailIdLayout = findViewById(R.id.edEmailIdLayout)
        passEdt = findViewById(R.id.editTextPassword)
        loginBtn = findViewById(R.id.loginBtn)
        forgotPasswordTextView = findViewById(R.id.textView2)
        registerNowTextView = findViewById(R.id.textView4)

        edEmailId.addTextChangedListener(createTextWatcher(edEmailIdLayout, ::validEmail, "Enter a valid e-mail!"))
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
        val emailId = edEmailId.text.toString().trim()
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
    private fun createTextWatcher(layout: TextInputLayout, validator: (String) -> Boolean, errorMsg: String) = object :
        TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if (s != null && validator(s.toString().trim())) {
                layout.error = null
            } else {
                layout.error = errorMsg
            }
        }
    }

    // Validation functions
    private fun validEmail(email: String): Boolean {
        val emailPattern = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        return email.matches(emailPattern) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}