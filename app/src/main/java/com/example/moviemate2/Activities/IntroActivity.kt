package com.example.moviemate2.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.moviemate2.R

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val getInBtn: Button = findViewById(R.id.getInBtn)

        getInBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
