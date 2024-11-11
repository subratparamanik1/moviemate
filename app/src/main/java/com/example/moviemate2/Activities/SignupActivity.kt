package com.example.moviemate2.Activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moviemate2.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var edUserNameLayout: TextInputLayout
    private lateinit var edEmailIdLayout: TextInputLayout
    private lateinit var edPhoneNumLayout: TextInputLayout
    private lateinit var edPasswordLayout: TextInputLayout
    private lateinit var edConPasswordLayout: TextInputLayout
    private lateinit var edUserName: TextInputEditText
    private lateinit var edEmailId: TextInputEditText
    private lateinit var edPhoneNum: TextInputEditText
    private lateinit var edPassword: TextInputEditText
    private lateinit var edConPassword: TextInputEditText
    private lateinit var signUpBtn: Button
    private lateinit var loadingDialog: Dialog

    private var isValidPassword = false
    private var isValidConPassword = false
    private lateinit var verificationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize views
        auth = FirebaseAuth.getInstance()
        edUserNameLayout = findViewById(R.id.edUserNameLayout)
        edEmailIdLayout = findViewById(R.id.edEmailIdLayout)
        edPhoneNumLayout = findViewById(R.id.edPhoneNumLayout)
        edPasswordLayout = findViewById(R.id.edPasswordLayout)
        edConPasswordLayout = findViewById(R.id.edConPasswordLayout)
        edUserName = findViewById(R.id.edUserName)
        edEmailId = findViewById(R.id.edEmailId)
        edPhoneNum = findViewById(R.id.edPhoneNum)
        edPassword = findViewById(R.id.edPassword)
        edConPassword = findViewById(R.id.edConPassword)
        signUpBtn = findViewById(R.id.signUpBtn)

        // Set up TextWatchers for validation
        edUserName.addTextChangedListener(createTextWatcher(edUserNameLayout, ::validUsername, "Invalid username. Use 3-20 characters, letters, numbers, underscores, or hyphens."))
        edEmailId.addTextChangedListener(createTextWatcher(edEmailIdLayout, ::validEmail, "Enter a valid e-mail!"))
        edPhoneNum.addTextChangedListener(createTextWatcher(edPhoneNumLayout, ::validPhoneNo, "Enter a valid phone number!"))
        edPassword.addTextChangedListener(createTextWatcher(edPasswordLayout, ::validatePassword, "Password must be at least 8 characters!"))
        edConPassword.addTextChangedListener(createTextWatcher(edConPasswordLayout, ::validateConfirmPassword, "Passwords don't match!"))

        signUpBtn.setOnClickListener { validation() }

        loadingDialog = Dialog(this)
        loadingDialog.setContentView(R.layout.activity_signup)
        loadingDialog.window!!.setLayout(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        loadingDialog.setCancelable(false)
    }

    private fun validation() {
        val userName = edUserName.text.toString().trim()
        val emailId = edEmailId.text.toString().trim()
        val phoneNum = edPhoneNum.text.toString().trim()
        val password = edPassword.text.toString().trim()
        val conPassword = edConPassword.text.toString().trim()

        // Perform validation checks
        if (userName.isEmpty() || !validUsername(userName)) {
            edUserNameLayout.error = "Invalid username. Use 3-20 characters, letters, numbers, underscores, or hyphens."
        } else if (emailId.isEmpty() || !validEmail(emailId)) {
            edEmailIdLayout.error = "Enter a valid e-mail!"
        } else if (phoneNum.isEmpty() || !validPhoneNo(phoneNum)) {
            edPhoneNumLayout.error = "Enter a valid phone number!"
        } else if (!validatePassword(password)) {
            edPasswordLayout.error = "Password must be at least 8 characters!"
        } else if (password != conPassword) {
            edConPasswordLayout.error = "Passwords don't match!"
        } else {
            // Firebase user creation logic
            auth.createUserWithEmailAndPassword(emailId, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        val database = FirebaseDatabase.getInstance().reference
                        val userMap = mapOf(
                            "userName" to userName,
                            "emailId" to emailId,
                            "phoneNum" to phoneNum
                        )
                        if (uid != null) {
                            database.child("users").child(uid).setValue(userMap)
                                .addOnCompleteListener { storeTask ->
                                    if (!storeTask.isSuccessful) {
                                        Toast.makeText(this, "Failed to store user data: ${storeTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    } else {
                                        sendOtp(phoneNum)
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(this, "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun sendOtp(phoneNum: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNum)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {}
                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@SignupActivity, "Failed to send OTP: ${e.message}", Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    val intent = Intent(this@SignupActivity, OTPVerificationActivity::class.java)
                    intent.putExtra("phoneNumber", phoneNum)
                    intent.putExtra("verificationId", verificationId)
                    startActivity(intent)
                }
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // TextWatcher for each field
    private fun createTextWatcher(layout: TextInputLayout, validator: (String) -> Boolean, errorMsg: String) = object : TextWatcher {
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


    private fun validPhoneNo(phoneNo: String): Boolean {
        val regex = Regex("^[6-9][0-9]{9}$") // Ensures the number starts with 6-9 and followed by 9 digits
        return phoneNo.matches(regex)
    }


    private fun validUsername(username: String): Boolean {
        val usernamePattern = "^[a-zA-Z0-9](?!.*[_-]{2})[a-zA-Z0-9_-]{1,18}[a-zA-Z0-9]$"
        return username.matches(Regex(usernamePattern))
    }

    private fun validatePassword(password: String): Boolean {
        return password.length >= 8
    }

    private fun validateConfirmPassword(conPassword: String): Boolean {
        return conPassword == edPassword.text.toString()
    }
}
