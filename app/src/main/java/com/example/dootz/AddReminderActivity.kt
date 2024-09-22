package com.example.dootz

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class AddReminderActivity : AppCompatActivity() {

    private lateinit var selectedDate: String
    private lateinit var selectedTime: String
    private var taskPosition: Int? = null
    private var calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)

        val title: EditText = findViewById(R.id.etTitle)
        val description: EditText = findViewById(R.id.etDescription)
        val btnSelectDate: Button = findViewById(R.id.btnSelectDate)
        val btnSelectTime: Button = findViewById(R.id.btnSelectTime)
        val btnAddReminder: Button = findViewById(R.id.btnAddReminder)

        // Check is editing  task
        val intent = intent
        if (intent.hasExtra("taskTitle")) {
            title.setText(intent.getStringExtra("taskTitle"))
            description.setText(intent.getStringExtra("taskDescription"))
            selectedDate = intent.getStringExtra("taskDate").orEmpty()
            selectedTime = intent.getStringExtra("taskTime").orEmpty()
            taskPosition = intent.getIntExtra("taskPosition", -1)

            btnSelectDate.text = selectedDate
            btnSelectTime.text = selectedTime
        }

        // Date picker
        btnSelectDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                calendar.set(selectedYear, selectedMonth, selectedDay)
                btnSelectDate.text = selectedDate   // Show the selected date on the button
            }, year, month, day)

            datePickerDialog.show()
        }

        // Time picker
        btnSelectTime.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                selectedTime = "$selectedHour:$selectedMinute"
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                btnSelectTime.text = selectedTime  // Show the selected time on the button
            }, hour, minute, true)

            timePickerDialog.show()
        }

        // Handle the "Add Reminder" button click to save the reminder
        btnAddReminder.setOnClickListener {
            val taskTitle = title.text.toString()
            val taskDescription = description.text.toString()

            // Validate inputs
            if (taskTitle.isEmpty() || taskDescription.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save task to SharedPreferences
            saveTaskToSharedPreferences(taskTitle, taskDescription)

            // Schedule the alarm to trigger the notification
            scheduleNotification(taskTitle, taskDescription, calendar)

            // Return to home activity
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun saveTaskToSharedPreferences(taskTitle: String, taskDescription: String) {
        val sharedPreferences = getSharedPreferences("DootzTasks", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val tasksJson = sharedPreferences.getString("tasks", "[]") ?: "[]"
        val taskListType = object : TypeToken<MutableList<Task>>() {}.type
        val taskList: MutableList<Task> = gson.fromJson(tasksJson, taskListType)

        // If taskPosition is null, it means we're adding a new task
        val newTask = Task(taskTitle, taskDescription, selectedDate, selectedTime)
        if (taskPosition != null && taskPosition != -1) {
            // Edit an existing task
            taskList[taskPosition!!] = newTask
        } else {
            // Add a new task
            taskList.add(newTask)
        }

        // Save updated task list to SharedPreferences as JSON
        val updatedTasksJson = gson.toJson(taskList)
        editor.putString("tasks", updatedTasksJson)
        editor.apply()
    }

    private fun scheduleNotification(taskTitle: String, taskDescription: String, calendar: Calendar) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Prepare the intent to trigger the BroadcastReceiver
        val intent = Intent(this, TaskReminderReceiver::class.java).apply {
            putExtra("taskTitle", taskTitle)
        }


        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set the alarm to trigger at the exact date and time
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(this, "Reminder set for $selectedDate at $selectedTime", Toast.LENGTH_SHORT).show()
    }

}
