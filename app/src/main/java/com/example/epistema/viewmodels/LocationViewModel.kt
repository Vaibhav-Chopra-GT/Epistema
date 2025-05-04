package com.example.epistema.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import androidx.lifecycle.viewModelScope
import com.example.epistema.data.GeoArticle
import com.example.epistema.network.RetrofitInstance
import com.example.epistema.util.LocationUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val _nearbyArticles = MutableStateFlow<List<GeoArticle>>(emptyList())
    val nearbyArticles = _nearbyArticles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        fetchNearbyArticles()
    }

    private fun fetchNearbyArticles() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val location = LocationUtil.getLastKnownLocation(getApplication())
                if (location != null) {
                    val coord = "${location.latitude}|${location.longitude}"
                    val response = RetrofitInstance.api.getNearbyArticles(coord = coord)
                    _nearbyArticles.value = response.query.geosearch
                } else {
                    _error.value = "Unable to get location."
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun fetchArticleContentByTitle(title: String): String {
        return try {
            val response = RetrofitInstance.api.parseArticleByTitle(page = title)
            val htmlText = response.parse.text.html

            // Parse with Jsoup to extract only <p> tags (main article content)
            val document: Document = Jsoup.parse(htmlText)
            val paragraphs: Elements = document.select("p")

            // Join paragraph texts, filter out empty lines
            val cleanText = paragraphs.joinToString("\n\n") { it.text() }.trim()
            cleanText.ifEmpty { "No readable content found." }
        } catch (e: Exception) {
            "Error loading article content: ${e.localizedMessage}"
        }
    }

}
