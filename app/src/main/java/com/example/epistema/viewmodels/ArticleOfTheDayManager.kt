package com.example.epistema.viewmodels

//package com.example.epistema.util

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ArticleOfTheDayManager {
    private const val PREFS_NAME = "article_prefs"
    private const val KEY_TITLE = "article_title"
    private const val KEY_PAGE_ID = "article_page_id"
    private const val KEY_DATE = "article_date"

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodayArticle(context: Context): Pair<String, Int>? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedDate = prefs.getString(KEY_DATE, "")
        val today = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

        return if (savedDate == today) {
            val title = prefs.getString(KEY_TITLE, null)
            val pageId = prefs.getInt(KEY_PAGE_ID, -1)
            if (title != null && pageId != -1) title to pageId else null
        } else {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveTodayArticle(context: Context, title: String, pageId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(KEY_TITLE, title)
        editor.putInt(KEY_PAGE_ID, pageId)
        editor.putString(KEY_DATE, LocalDate.now().format(DateTimeFormatter.ISO_DATE))
        editor.apply()
    }
}
