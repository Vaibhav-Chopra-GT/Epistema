package com.example.epistema.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GlobalStateViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentArticleId = MutableStateFlow(-1)
    private val _currentPageId = MutableStateFlow(-1)

    val currentArticleId = _currentArticleId.asStateFlow()
    val currentPageId = _currentPageId.asStateFlow()

    fun setCurrentArticle(id: Int) {
        viewModelScope.launch {
            _currentArticleId.emit(id)
            _currentPageId.emit(-1)
        }
    }

    fun setCurrentPage(id: Int) {
        viewModelScope.launch {
            _currentPageId.emit(id)
            _currentArticleId.emit(-1)
        }
    }

    fun clearState() {
        viewModelScope.launch {
            _currentArticleId.emit(-1)
            _currentPageId.emit(-1)
        }
    }
}