package com.example.epistema

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import com.example.epistema.ui.SearchScreen
import com.example.epistema.ui.theme.EpistemaTheme
import androidx.compose.ui.Modifier

class Activity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EpistemaTheme {
                AppScaffold(selectedIndex = 1) { innerPadding ->
                    SearchScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
