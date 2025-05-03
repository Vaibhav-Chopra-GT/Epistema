package com.example.epistema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.example.epistema.ui.ProfileScreen
import com.example.epistema.ui.theme.EpistemaTheme
import com.example.epistema.viewmodels.SearchViewModel

class Activity3 : ComponentActivity() {

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pageId = intent.getIntExtra("PAGE_ID", -1)

        setContent {
            EpistemaTheme {
                LaunchedEffect(pageId) {
                    if (pageId != -1) {
                        // Updated method name:
                        viewModel.loadArticleById(pageId)
//                        viewModel.logHistory(pageId)
                    }
                }

                AppScaffold(selectedIndex = 2) { innerPadding ->
                    ProfileScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel,

                    )
                }
            }
        }
    }
}
