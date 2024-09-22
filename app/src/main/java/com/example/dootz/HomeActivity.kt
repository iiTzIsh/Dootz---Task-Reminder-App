package com.example.dootz

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeActivity : AppCompatActivity(), TaskAdapter.TaskAdapterListener {

    private lateinit var taskList: MutableList<Task>
    private lateinit var taskAdapter: TaskAdapter
    private val REQUEST_NOTIFICATION_PERMISSION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Set up RecyclerView and FloatingActionButton
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val fabAdd: FloatingActionButton = findViewById(R.id.fab_add)


        taskList = mutableListOf()
        taskAdapter = TaskAdapter(taskList, this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        // Load tasks from SharedPreferences
        loadTasks()


        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddReminderActivity::class.java))
        }

        checkNotificationPermission()
    }

    private fun loadTasks() {
        val sharedPreferences = getSharedPreferences("DootzTasks", MODE_PRIVATE)
        val gson = Gson()
        val tasksJson = sharedPreferences.getString("tasks", "[]")
        val taskListType = object : TypeToken<MutableList<Task>>() {}.type
        taskList.clear()
        taskList.addAll(gson.fromJson(tasksJson, taskListType))

        taskAdapter.notifyDataSetChanged()


        DootzWidgetProvider.updateWidgetData(this)
    }

    private fun saveTasks() {
        val sharedPreferences = getSharedPreferences("DootzTasks", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val updatedTasksJson = gson.toJson(taskList)
        editor.putString("tasks", updatedTasksJson)
        editor.apply()

        // Update the widget when tasks saved
        DootzWidgetProvider.updateWidgetData(this)
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationHelper.showNotification(this, "Permission Granted", "You can now receive reminders.")
            } else {
                Toast.makeText(this, "Notifications will not work without permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditClick(position: Int) {
        val task = taskList[position]
        val intent = Intent(this, AddReminderActivity::class.java)
        intent.putExtra("taskTitle", task.title)
        intent.putExtra("taskDescription", task.description)
        intent.putExtra("taskTime", task.time)
        intent.putExtra("taskPosition", position)
        startActivity(intent)
    }

    override fun onDeleteClick(position: Int) {
        taskList.removeAt(position)
        taskAdapter.notifyItemRemoved(position)
        saveTasks()
        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
    }

    override fun onTaskChecked(position: Int, isChecked: Boolean) {
        taskList[position].isCompleted = isChecked
        saveTasks()
        Toast.makeText(this, if (isChecked) "Task completed" else "Task uncompleted", Toast.LENGTH_SHORT).show()
    }
}
