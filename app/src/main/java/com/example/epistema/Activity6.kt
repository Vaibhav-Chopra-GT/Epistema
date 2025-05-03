package com.example.epistema

import ArticleListScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.example.epistema.ui.theme.EpistemaTheme

class Activity6 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val category = intent.getStringExtra("CATEGORY") ?: "Science"  // default fallback
        setContent {
            EpistemaTheme {
                AppScaffold(selectedIndex = 7) { innerPadding ->
                    ArticleListScreen(modifier = Modifier.padding(innerPadding), category)
                }
            }
        }
    }
}
