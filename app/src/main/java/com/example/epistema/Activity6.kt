package com.example.epistema

import ArticleListScreen
import EpistemaTheme
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier

class Activity6 : ComponentActivity() {
    private val globalStateVm by lazy { (application as EpistemaApp).globalStateViewModel }
    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val category = intent.getStringExtra("CATEGORY") ?: "Science"  // default fallback
        setContent {
            EpistemaTheme (
                themeOverride = globalStateVm.appTheme.value,
                fontSize = globalStateVm.fontSize.value
            ) {
                AppScaffold(selectedIndex = 7) { innerPadding ->
                    ArticleListScreen(modifier = Modifier.padding(innerPadding), category)
                }
            }
        }
    }
}
