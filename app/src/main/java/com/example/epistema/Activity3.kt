package com.example.epistema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.epistema.ui.ProfileScreen
import com.example.epistema.ui.theme.EpistemaTheme
import com.example.epistema.viewmodels.SavedArticlesViewModel
import com.example.epistema.viewmodels.GlobalStateViewModel
import com.example.epistema.viewmodels.SearchViewModel

class Activity3 : ComponentActivity() {
    private val app by lazy { application as EpistemaApp }
    private val globalStateVm by lazy { app.globalStateViewModel }
    private val searchVm: SearchViewModel by viewModels()
    private val savedArticlesVm: SavedArticlesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EpistemaTheme {
                val globalArticleId by globalStateVm.currentArticleId.collectAsState()
                val globalPageId by globalStateVm.currentPageId.collectAsState()

                var offlineContent by remember { mutableStateOf("") }
                var offlineTitle by remember { mutableStateOf("") }

                // Handle initial intent
                LaunchedEffect(Unit) {
                    intent?.let {
                        when {
                            it.hasExtra("ARTICLE_ID") -> {
                                val id = it.getIntExtra("ARTICLE_ID", -1)
                                if (id != -1) globalStateVm.setCurrentArticle(id)
                            }

                            it.hasExtra("PAGE_ID") -> {
                                val id = it.getIntExtra("PAGE_ID", -1)
                                if (id != -1) globalStateVm.setCurrentPage(id)
                            }
                        }
                    }
                }

                // Load content
                LaunchedEffect(globalArticleId, globalPageId) {
                    when {
                        globalArticleId != -1 -> {
                            savedArticlesVm.getArticleById(globalArticleId)?.let {
                                offlineTitle = it.title
                                offlineContent = it.content
                            }
                        }

                        globalPageId != -1 -> {
                            searchVm.loadArticleById(globalPageId)
                        }
                    }
                }

                if (globalArticleId == -1 && globalPageId == -1) {
                    NoArticleScreen(
                        onBack = { finish() }
                    )
                } else {
                    AppScaffold(selectedIndex = 2) { innerPadding ->
                        ProfileScreen(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = searchVm,
                            savedVm = savedArticlesVm,
                            offlineTitle = offlineTitle,
                            offlineContent = offlineContent,
                            onArticleClosed = {
                                globalStateVm.clearState()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NoArticleScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "No Article Selected",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Select an article from your saved items or search results",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
    }
}

