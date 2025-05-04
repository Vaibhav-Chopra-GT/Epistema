// com/example/epistema/viewmodels/HistoryViewModel.kt
package com.example.epistema.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.epistema.data.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HistoryViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = HistoryRepository(app)

    // Expose history as StateFlow
    private val _historyFlow = MutableStateFlow(repo.getHistory())
    val historyFlow: StateFlow<List<HistoryRepository.HistoryEntry>> = _historyFlow.asStateFlow()

    // Refresh from repository
    fun refresh() {
        _historyFlow.value = repo.getHistory()
    }

    // Record only pageId; title and image fetched dynamically in UI
    fun record(pageId: Int) {
        repo.addToHistory(HistoryRepository.HistoryEntry(pageId))
        refresh()
    }
    fun clearHistory() {
        repo.clearHistory()
        refresh()
    }


    init {
        refresh()
    }
}
