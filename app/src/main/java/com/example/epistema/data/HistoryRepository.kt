package com.example.epistema.data

// com/example/epistema/data/HistoryRepository.kt


import android.content.Context
import com.example.epistema.Article
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryRepository(context: Context) {
    private val prefs = context.getSharedPreferences("article_history", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val type = object : TypeToken<List<HistoryEntry>>() {}.type

    data class HistoryEntry(
        val pageId: Int,
//        val title: String,
//        val imageUrl: String?
    )

    fun getHistory(): List<HistoryEntry> {
        val json = prefs.getString("history_list", null) ?: return emptyList()
        return gson.fromJson(json, type)
    }

    fun addToHistory(entry: HistoryEntry) {
        // load, prepend, dedupe, truncate to 10
        val current = getHistory().toMutableList()
        current.removeAll { it.pageId == entry.pageId }
        current.add(0, entry)
        if (current.size > 10) current.subList(10, current.size).clear()
        prefs.edit()
            .putString("history_list", gson.toJson(current))
            .apply()
    }
    fun clearHistory() {
        prefs.edit().remove("history_list").apply()
    }

}
