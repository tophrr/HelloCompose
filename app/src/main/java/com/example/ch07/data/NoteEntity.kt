package com.example.ch07.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Satu objek = satu baris tabel "notes". Skema awal (versi 1).
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    // Waktu terakhir isi catatan diubah
    val updatedAt: Long = createdAt,
    val isPinned: Boolean = false,
    // Tag/label catatan
    val tags: List<String> = emptyList()
)
