package com.example.dootz

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskTitle = intent.getStringExtra("taskTitle")
        val taskDescription = intent.getStringExtra("taskDescription")

        // Log when receiver trigger
        Log.d("TaskReminderReceiver", "Notification triggered for task: $taskTitle")

        val notificationId = System.currentTimeMillis().toInt()
        val notificationBuilder = NotificationCompat.Builder(context, "task_reminder_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(taskTitle)
            .setContentText(taskDescription)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

}
