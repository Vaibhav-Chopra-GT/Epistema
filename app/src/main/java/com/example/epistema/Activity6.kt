package com.example.epistema

import ArticleListScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import com.example.epistema.ui.SavedArticlesScreen
import com.example.epistema.ui.LocationsScreen

import com.example.epistema.ui.theme.EpistemaTheme
import androidx.compose.ui.Modifier

class Activity6 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EpistemaTheme {
                AppScaffold(selectedIndex = 7) { innerPadding ->
                    ArticleListScreen(modifier = Modifier.padding(innerPadding),"Science")
                }
            }
        }
    }
}
