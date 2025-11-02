package com.example.taskcalendarapp

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf

data class Task(
    val id: Int,
    val title: String,
    val isCompleted: Boolean = false
)

class TasksViewModel : ViewModel() {
    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> = _tasks

    init {
        addTask("Complete homework")
        addTask("Buy groceries")
        addTask("Call dentist")
    }

    fun addTask(title: String) {
        val newId = (_tasks.maxOfOrNull { it.id } ?: -1) + 1
        _tasks.add(Task(newId, title, false))
    }

    fun toggleTask(id: Int) {
        val task = _tasks.find { it.id == id }
        task?.let {
            val index = _tasks.indexOf(it)
            _tasks[index] = it.copy(isCompleted = !it.isCompleted)
        }
    }

    fun deleteTask(id: Int) {
        _tasks.removeIf { it.id == id }
    }

    fun getTaskById(id: Int): Task? {
        return _tasks.find { it.id == id }
    }
}