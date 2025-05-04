package com.example.epistema

import android.app.Application
import com.example.epistema.viewmodels.GlobalStateViewModel

class EpistemaApp : Application() {
    val globalStateViewModel by lazy { GlobalStateViewModel(this) }
}