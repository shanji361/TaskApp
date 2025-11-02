package com.example.taskcalendarapp

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

class NotesViewModel : ViewModel() {
    private val _notes = mutableStateListOf<Note>()
    val notes: List<Note> = _notes


    fun addNote(title: String, content: String) {
        val newId = (_notes.maxOfOrNull { it.id } ?: -1) + 1
        _notes.add(0, Note(newId, title, content))
    }

    fun deleteNote(id: Int) {
        _notes.removeIf { it.id == id }
    }

    fun getNoteById(id: Int): Note? {
        return _notes.find { it.id == id }
    }
}