package com.example.epistema

import ArticleListScreen
import SettingsScreen
import android.os.Bundle

import androidx.activity.compose.setContent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.epistema.ui.ArticleDetailScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "articleList") {
        composable("articleList") {
            ArticleListScreen(navController, section = "Science")
        }
        composable("articleDetail/{articleTitle}") { backStackEntry ->
            val articleTitle = backStackEntry.arguments?.getString("articleTitle") ?: "Unknown"
            ArticleDetailScreen(articleTitle, navController)
        }
        composable("settings") {
            SettingsScreen()
        }

    }
}