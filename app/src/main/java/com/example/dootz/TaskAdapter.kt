package com.example.dootz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val taskList: MutableList<Task>, private val listener: TaskAdapterListener) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    interface TaskAdapterListener {
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
        fun onTaskChecked(position: Int, isChecked: Boolean)
    }

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTaskTitle)
        val description: TextView = view.findViewById(R.id.tvTaskDescription)
        val time: TextView = view.findViewById(R.id.tvTaskTime)
        val checkBox: CheckBox = view.findViewById(R.id.taskCheckBox)
        val editButton: ImageButton = view.findViewById(R.id.btnEditTask)
        val deleteButton: ImageButton = view.findViewById(R.id.btnDeleteTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.title.text = task.title
        holder.description.text = task.description
        holder.time.text = "Time: ${task.time}"

        holder.checkBox.isChecked = task.isCompleted

        holder.editButton.setOnClickListener {
            listener.onEditClick(position)
        }

        holder.deleteButton.setOnClickListener {
            listener.onDeleteClick(position)
        }

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            listener.onTaskChecked(position, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }
}
