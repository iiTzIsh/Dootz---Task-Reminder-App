package com.example.dootz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class NextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_next)

        val nextButton: Button = findViewById(R.id.next_button)
        nextButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }
}
