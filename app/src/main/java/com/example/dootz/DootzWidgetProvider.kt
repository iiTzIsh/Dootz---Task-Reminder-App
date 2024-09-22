package com.example.dootz

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import org.json.JSONArray

class DootzWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            // Set click handler to open  app when clicked
            val intent = Intent(context, HomeActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.tvWidgetTitle, pendingIntent)

            // Fetch the saved tasks
            val sharedPreferences = context.getSharedPreferences("DootzTasks", Context.MODE_PRIVATE)
            val tasksString = sharedPreferences.getString("tasks", "[]") ?: "[]"

            // Parse the tasks from JSON
            val tasksArray = JSONArray(tasksString)


            val formattedTasks = StringBuilder()
            for (i in 0 until tasksArray.length()) {
                val taskObj = tasksArray.getJSONObject(i)
                val title = taskObj.optString("title", "No Title")
                val date = taskObj.optString("date", "No Date")
                val time = taskObj.optString("time", "No Time")

                formattedTasks.append("$title\n$date | $time\n\n")  // Add space between tasks
            }

            // Set tasks
            views.setTextViewText(R.id.tvWidgetReminders, formattedTasks.toString().trim())

            // Update widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    companion object {

        fun updateWidgetData(context: Context) {
            val intent = Intent(context, DootzWidgetProvider::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(
                ComponentName(context, DootzWidgetProvider::class.java)
            )
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }
}
