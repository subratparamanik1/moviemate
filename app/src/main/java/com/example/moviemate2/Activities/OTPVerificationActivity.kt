package com.example.moviemate2.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moviemate2.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class OTPVerificationActivity : AppCompatActivity() {

    private lateinit var otpInput: TextInputEditText
    private lateinit var verifyOtpBtn: TextView
    private lateinit var resendOtpTextView: TextView
    private lateinit var otpSentInfo: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    private var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpverification)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        otpInput = findViewById(R.id.editTextOtp)
        verifyOtpBtn = findViewById(R.id.verifyOtpBtn)
        resendOtpTextView = findViewById(R.id.resendOtpTextView)
        otpSentInfo = findViewById(R.id.otpSentTextView)

        // Get phone number passed from previous activity
        phoneNumber = intent.getStringExtra("phoneNumber")

        // Update masked phone number in the TextView
        phoneNumber?.let {
            val maskedNumber = it.substring(0, 2) + "******" + it.substring(8)
            otpSentInfo.text = "OTP has been sent to $maskedNumber"
        }

        // Send OTP to the phone number
        phoneNumber?.let {
            sendOtp(it)
        }

        // Verify OTP button click listener
        verifyOtpBtn.setOnClickListener {
            val otp = otpInput.text.toString()
            if (otp.isNotEmpty()) {
                verifyOtp(otp)
            } else {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }

        // Resend OTP click listener
        resendOtpTextView.setOnClickListener {
            phoneNumber?.let { resendOtp(it) }
        }
    }

    private fun sendOtp(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@OTPVerificationActivity, "Failed to send OTP: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("OTPVerification", "onVerificationFailed: ${e.message}")
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    storedVerificationId = verificationId
                    resendToken = token
                }
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOtp(otp: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun resendOtp(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@OTPVerificationActivity, "Failed to resend OTP: ${e.message}", Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    storedVerificationId = verificationId
                    resendToken = token
                }
            }).setForceResendingToken(resendToken).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "OTP verified successfully", Toast.LENGTH_SHORT).show()
                // Proceed to the next activity
            } else {
                Toast.makeText(this, "OTP verification failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
