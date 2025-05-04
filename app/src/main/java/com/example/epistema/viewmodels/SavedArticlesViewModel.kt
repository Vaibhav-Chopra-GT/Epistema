package com.example.epistema.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.epistema.data.AppDatabase
import com.example.epistema.data.SavedArticle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.File
import java.net.URL

sealed class SaveState {
    object Idle : SaveState()
    data class Progress(val current: Int, val total: Int) : SaveState()
    object Success : SaveState()
    data class Error(val message: String) : SaveState()
}

class SavedArticlesViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).savedArticleDao()
    private val context = getApplication<Application>().applicationContext

    val savedArticles: StateFlow<List<SavedArticle>> =
        dao.getAll().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState

    // New: Get article by ID
    suspend fun getArticleById(id: Int): SavedArticle? = withContext(Dispatchers.IO) {
        try {
            dao.getArticleById(id)
        } catch (e: Exception) {
            Log.e("SavedVM", "Error getting article $id", e)
            null
        }
    }

    fun saveArticle(title: String, content: String) {
        viewModelScope.launch {
            _saveState.value = SaveState.Progress(0, 1)
            try {
                val imageDir = File(context.filesDir, "saved_images").apply {
                    if (!exists()) mkdirs()
                }

                val doc = Jsoup.parse(content)
                val images = doc.select("img")
                val total = images.size

                if (total > 0) _saveState.value = SaveState.Progress(0, total)

                images.forEachIndexed { index, img ->
                    processImage(img, imageDir)
                    _saveState.value = SaveState.Progress(index + 1, total)
                }

                val modifiedContent = doc.html()
                dao.insert(SavedArticle(title = title, content = modifiedContent))
                _saveState.value = SaveState.Success
            } catch (e: Exception) {
                Log.e("SaveArticle", "Save failed for '$title'", e)
                _saveState.value = SaveState.Error("Save failed: ${e.localizedMessage}")
            }
        }
    }

    fun deleteAllArticles() {
        viewModelScope.launch {
            try {
                // Clear both DB and stored images
                dao.clearAllData()
                File(context.filesDir, "saved_images").deleteRecursively()
            } catch (e: Exception) {
                Log.e("SavedVM", "Failed to clear data", e)
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }

    private suspend fun processImage(img: org.jsoup.nodes.Element, outputDir: File) {
        withContext(Dispatchers.IO) {
            try {
                val src = img.attr("src")
                val url = when {
                    src.startsWith("//") -> "https:$src"
                    src.startsWith("/") -> "https://en.wikipedia.org$src"
                    else -> src
                }

                val fileName = "${System.currentTimeMillis()}_${url.hashCode()}.jpg"
                val outputFile = File(outputDir, fileName)

                // Download and save image
                URL(url).openStream().use { input ->
                    outputFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                val relativePath = "saved_images/$fileName"
                img.attr("src", relativePath) // ✅ Correct: "saved_images/123.jpg"

                // Log the stored path
                Log.d("ImageDebug", "Stored path: $relativePath")
            } catch (e: Exception) {
                Log.e("ProcessImage", "Failed to process: ${img.attr("src")}", e)
                throw e
            }
        }
    }

    fun deleteArticle(article: SavedArticle) {
        viewModelScope.launch {
            try {
                // Delete associated images
                val doc = Jsoup.parse(article.content)
                val images = doc.select("img")
                val imageDir = File(context.filesDir, "saved_images")

                images.forEach { img ->
                    val src = img.attr("src")
                    val fileName = src.substringAfter("saved_images/")
                    File(imageDir, fileName).takeIf { it.exists() }?.delete()
                }

                // Delete from database
                withContext(Dispatchers.IO) {
                    dao.deleteById(article.id)
                }
            } catch (e: Exception) {
                Log.e("SavedVM", "Failed to delete article ${article.id}", e)
            }
        }
    }
}