package com.example.epistema

import EpistemaTheme
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import com.example.epistema.ui.SavedArticlesScreen
import com.example.epistema.ui.LocationsScreen

import androidx.compose.ui.Modifier

class Activity5 : ComponentActivity() {
    private val globalStateVm by lazy { (application as EpistemaApp).globalStateViewModel }
    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EpistemaTheme(
                themeOverride = globalStateVm.appTheme.value,
                fontSize = globalStateVm.fontSize.value
            ) {
                AppScaffold(selectedIndex = 4) { innerPadding ->
                    SavedArticlesScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
