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

    // Declare views and Firebase instances
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

        signUpBtn.setOnClickListener { validation() }

        // Password validation listeners
        edPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditTextPassword(edPasswordLayout, edPassword.text.toString(), "password")
            }
        })

        edConPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditTextPassword(edConPasswordLayout, edConPassword.text.toString(), "conPassword")
            }
        })

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
        if (userName.isEmpty()) {
            edUserNameLayout.error = "Required"
        } else if (emailId.isEmpty()) {
            edEmailIdLayout.error = "Required"
        } else if (!validEmail(emailId)) {
            edEmailIdLayout.error = "Enter valid e-mail!"
        } else if (phoneNum.isEmpty()) {
            edPhoneNumLayout.error = "Required"
        } else if (!validPhoneNo(phoneNum)) {
            edPhoneNumLayout.error = "Enter valid phone no.!"
        } else {
            validateEditTextPassword(edPasswordLayout, password, "password")
            if (isValidPassword) {
                validateEditTextPassword(edConPasswordLayout, conPassword, "conPassword")
                if (isValidConPassword) {
                    if (password != conPassword) {
                        edConPasswordLayout.error = "Passwords don't match!"
                    } else {
                        edConPasswordLayout.error = null

                        // Create user with email and password
                        auth.createUserWithEmailAndPassword(emailId, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Store user details
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
                                                    // Send OTP after successful registration
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
            }
        }
    }

    private fun sendOtp(phoneNum: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNum)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Direct automatic OTP completion is handled in OTPVerificationActivity
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@SignupActivity, "Failed to send OTP: ${e.message}", Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    // Save verification ID and start OTP verification activity
                    val intent = Intent(this@SignupActivity, OTPVerificationActivity::class.java)
                    intent.putExtra("phoneNumber", phoneNum)
                    intent.putExtra("verificationId", verificationId)
                    startActivity(intent)
                }
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun validateEditTextPassword(
        textInputLayout: TextInputLayout,
        text: String,
        state: String
    ) {
        var truthState = false
        when {
            text.trim().isEmpty() -> {
                textInputLayout.error = "Required"
            }
            text.length < 8 -> {
                textInputLayout.error = "Password must be at least 8 characters!"
            }
            else -> {
                textInputLayout.error = null
                truthState = true
            }
        }
        if (truthState) {
            if (state == "password") {
                isValidPassword = true
            } else if (state == "conPassword") {
                isValidConPassword = true
            }
        } else {
            if (state == "password") {
                isValidPassword = false
            } else if (state == "conPassword") {
                isValidConPassword = false
            }
        }
    }

    private fun validEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validPhoneNo(phoneNo: String): Boolean {
        val regex = Regex("^\\+91[6-9][0-9]{9}\$|^[6-9][0-9]{9}\$")
        return phoneNo.matches(regex)
    }
}
