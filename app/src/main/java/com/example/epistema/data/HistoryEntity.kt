package com.example.epistema.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class HistoryEntry(
    @PrimaryKey val pageId: Int,
//    val title: String,
//    val imageResId: Int,
    val timestamp: Long = System.currentTimeMillis()
)
