package com.example.epistema

import EpistemaTheme
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.epistema.ui.HomeScreen

class MainActivity : ComponentActivity() {
    private var onVoiceResult: ((String) -> Unit)? = null

    private val voiceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            matches?.firstOrNull()?.let {
                onVoiceResult?.invoke(it)
            }
        }
    }
    private val globalStateVm by lazy { (application as EpistemaApp).globalStateViewModel }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val initialQuery = intent.getStringExtra("initial_search") ?: ""

        setContent {
            EpistemaTheme (
                themeOverride = globalStateVm.appTheme.value,
                fontSize = globalStateVm.fontSize.value
            ) {
                AppScaffold(selectedIndex = 0) { innerPadding ->
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding),
                        initialQuery = initialQuery,
                        onStartVoiceRecognition = { callback ->
                            onVoiceResult = callback
                            startVoiceRecognition()
                        }
                    )
                }
            }
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        }
        voiceLauncher.launch(intent)
    }
}
