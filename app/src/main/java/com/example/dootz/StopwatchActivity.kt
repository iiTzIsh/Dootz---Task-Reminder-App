package com.example.dootz

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.Chronometer
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.widget.ImageButton

class StopwatchActivity : AppCompatActivity() {

    private lateinit var chronometer: Chronometer
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var resetButton: Button

    private var isRunning = false
    private var timeWhenStopped: Long = 0
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stopwatch)

        chronometer = findViewById(R.id.chronometer)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        resetButton = findViewById(R.id.resetButton)

        val iconButton: ImageButton = findViewById(R.id.iconButton)

        iconButton.setOnClickListener {
            // Create an Intent to navigate to the SecondActivity
            val intent = Intent(this, HomeActivity::class.java)
            // Start the second activity
            startActivity(intent)
        }

        sharedPreferences = getSharedPreferences("StopwatchPrefs", MODE_PRIVATE)

        // Restore saved state
        timeWhenStopped = sharedPreferences.getLong("timeWhenStopped", 0L)
        isRunning = sharedPreferences.getBoolean("isRunning", false)   // SharedPreferences to save and restore stopwatch state

        if (isRunning) {
            chronometer.base = SystemClock.elapsedRealtime() + timeWhenStopped
            chronometer.start()
        } else {
            chronometer.base = SystemClock.elapsedRealtime() + timeWhenStopped
        }

        startButton.setOnClickListener {
            if (!isRunning) {
                chronometer.base = SystemClock.elapsedRealtime() + timeWhenStopped
                chronometer.start()
                isRunning = true
            }
        }

        stopButton.setOnClickListener {
            if (isRunning) {
                timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()
                chronometer.stop()
                isRunning = false
                saveStopwatchState()
            }
        }

        resetButton.setOnClickListener {
            chronometer.base = SystemClock.elapsedRealtime()
            timeWhenStopped = 0
            chronometer.stop()
            isRunning = false
            saveStopwatchState()
        }
    }

    // Save the stopwatch state when the activity is paused
    override fun onPause() {
        super.onPause()
        saveStopwatchState()
    }

    // Function to save the stopwatch state (timeWhenStopped and isRunning) to shared preferences
    private fun saveStopwatchState() {
        val editor = sharedPreferences.edit()
        editor.putLong("timeWhenStopped", timeWhenStopped)
        editor.putBoolean("isRunning", isRunning)
        editor.apply()
    }
}
