package com.example.epistema.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.epistema.data.ParseResponse
import com.example.epistema.data.WikiArticle
//import com.example.epistema.data.HistoryEntity
//import com.example.epistema.data.HistoryDao
//import com.example.epistema.data.HistoryEntry
import com.example.epistema.data.WikiSearchResult
import com.example.epistema.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val _searchResults = MutableStateFlow<List<WikiSearchResult>>(emptyList())
    private val _currentArticle = MutableStateFlow<WikiArticle?>(null)
    private val _isLoading = MutableStateFlow(false)

    val searchResults: StateFlow<List<WikiSearchResult>> = _searchResults.asStateFlow()
    val currentArticle: StateFlow<WikiArticle?> = _currentArticle.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun searchWikipedia(query: String) {
        viewModelScope.launch {
            _isLoading.update { true }
            try {
                val resp = RetrofitInstance.api.search(
                    action = "query",
                    format = "json",
                    list = "search",
                    query = query,
                    limit = 20
                )
                _searchResults.update { resp.query.search }
            } catch (_: Exception) {
                _searchResults.update { emptyList() }
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun loadArticleById(pageId: Int) {
        viewModelScope.launch {
            _isLoading.update { true }
            try {
                val resp: ParseResponse = RetrofitInstance.api.parseArticleById(
                    pageid = pageId.toString()
                )
                _currentArticle.update {
                    WikiArticle(
                        pageId = pageId,
                        title = resp.parse.title,
                        content = resp.parse.text.html,
                        url = null
                    )
                }
            } catch (_: Exception) {
                _currentArticle.update { null }
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun loadArticleByTitle(pageTitle: String) {
        viewModelScope.launch {
            _isLoading.update { true }
            try {
                val resp: ParseResponse = RetrofitInstance.api.parseArticleByTitle(
                    page = pageTitle
                )
                _currentArticle.update {
                    WikiArticle(
                        pageId = -1,
                        title = resp.parse.title,
                        content = resp.parse.text.html,
                        url = null
                    )
                }
            } catch (_: Exception) {
                _currentArticle.update { null }
            } finally {
                _isLoading.update { false }
            }
        }
    }


    fun clearSearchResults() {
        _searchResults.update { emptyList() }
    }
}
