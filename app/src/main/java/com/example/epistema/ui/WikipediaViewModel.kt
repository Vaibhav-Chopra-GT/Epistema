package com.example.epistema.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.epistema.Article
import com.example.epistema.WikipediaRepository


class WikipediaViewModel : ViewModel() {
    private val repository = WikipediaRepository()

    var articles by mutableStateOf<List<Article>>(emptyList())
        private set

    fun fetchArticles(section: String) {
        articles = repository.getArticlesBySection(section)
    }
}
