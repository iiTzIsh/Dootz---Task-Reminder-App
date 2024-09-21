package com.example.dootz

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class TaskReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Retrieve task name from intent
        val taskTitle = intent.getStringExtra("taskTitle") ?: "Task"

        // Create intent to open app when notification is clicked
        val notificationIntent = Intent(context, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Create notification
        val notificationBuilder = NotificationCompat.Builder(context, "task_reminder_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Task Reminder")
            .setContentText("It's time for: $taskTitle")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Trigger the notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
