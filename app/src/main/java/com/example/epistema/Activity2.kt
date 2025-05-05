package com.example.epistema

import EpistemaTheme
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import com.example.epistema.ui.HistoryScreen
import androidx.compose.ui.Modifier

//import com.example.epistema.viewmodel.HistoryViewModel
//import com.example.epistema.viewmodels.HistoryViewModel


class Activity2 : ComponentActivity() {

//    private val historyViewModel: HistoryViewModel by viewModels()

    private val globalStateVm by lazy { (application as EpistemaApp).globalStateViewModel }

    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EpistemaTheme(
                themeOverride = globalStateVm.appTheme.value,
                fontSize = globalStateVm.fontSize.value) {
                AppScaffold(selectedIndex = 1) { innerPadding ->
                    HistoryScreen(modifier = Modifier.padding(innerPadding))

                }
            }
        }
    }
}
