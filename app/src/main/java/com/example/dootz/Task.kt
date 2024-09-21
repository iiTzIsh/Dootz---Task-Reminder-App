package com.example.dootz

data class Task(
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    var isCompleted: Boolean = false
)
