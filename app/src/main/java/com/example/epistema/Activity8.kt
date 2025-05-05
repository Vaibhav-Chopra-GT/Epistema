package com.example.epistema

import ArticleListScreen
import EpistemaTheme
import SettingsScreen
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import com.example.epistema.ui.SavedArticlesScreen
import com.example.epistema.ui.LocationsScreen

import androidx.compose.ui.Modifier
import com.example.epistema.ui.AppContent

class Activity8 : ComponentActivity() {
    private val globalStateVm by lazy { (application as EpistemaApp).globalStateViewModel }

    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EpistemaTheme (
                themeOverride = globalStateVm.appTheme.value,
                fontSize = globalStateVm.fontSize.value
            ){
                AppScaffold(selectedIndex = 5) { innerPadding ->
                    AppContent(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}