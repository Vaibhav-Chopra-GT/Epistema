package com.example.epistema.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.epistema.localization.StringResources
import com.example.epistema.network.RetrofitInstance
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
    private val _appTheme = MutableStateFlow("Light")
    private val _fontSize = MutableStateFlow("Medium")
    private val _appLanguage = MutableStateFlow("English")

    val appTheme = _appTheme.asStateFlow()
    val fontSize = _fontSize.asStateFlow()
    val appLanguage = _appLanguage.asStateFlow()

    fun setAppTheme(theme: String) {
        viewModelScope.launch { _appTheme.emit(theme) }
    }

    fun setFontSize(size: String) {
        viewModelScope.launch { _fontSize.emit(size) }
    }

    fun setAppLanguage(lang: String) {
        viewModelScope.launch {
            _appLanguage.emit(lang)
            StringResources.setLanguage(lang)
        }
    }
}