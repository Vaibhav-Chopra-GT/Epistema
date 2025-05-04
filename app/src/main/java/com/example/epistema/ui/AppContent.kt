package com.example.epistema.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.example.epistema.MainActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var classificationResult by remember { mutableStateOf<String?>(null) }
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    val permissionState = rememberMultiplePermissionsState(permissions)

    val takePhotoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            Log.d("AppContent", "Image captured: $currentPhotoUri")
            currentPhotoUri?.let { uri ->
                imageUri = uri
                classificationResult = null
            }
        }
        else{
            Log.d("Error", "Photo Capture Failure")
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        classificationResult = null
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .offset(y = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            permissionState.allPermissionsGranted -> {
                Button(onClick = {
                    val file = createImageFile(context)
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    ).also { currentPhotoUri = it }
                    takePhotoLauncher.launch(uri)
                }) { Text("Take Photo") }

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    classificationResult = null
                    pickImageLauncher.launch("image/*")
                }) { Text("Choose from Gallery") }

                imageUri?.let { uri ->
                    Spacer(Modifier.height(16.dp))

                    val bitmap by remember(uri) {
                        derivedStateOf {
                            try {
                                context.contentResolver.openInputStream(uri)?.use { stream ->
                                    BitmapFactory.decodeStream(stream)
                                }
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }

                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap!!.asImageBitmap(),
                            contentDescription = "Selected image",
                            modifier = Modifier.size(224.dp)
                        )

                        LaunchedEffect(uri) {
                            val classifier = Classifier(context)
                            classificationResult = classifier.classifyImage(bitmap!!)
                            classifier.close()
                        }
                    } else {
                        Text("Error loading image")
                    }
                }

                classificationResult?.let { result ->
                    Spacer(Modifier.height(16.dp))
                    Text("Result: $result")
                    val intent = Intent(context, MainActivity::class.java).apply {
                        putExtra("initial_search", result)
                    }
                    context.startActivity(intent)
                }
            }


            permissionState.shouldShowRationale -> {
                Text("Permissions are required for this feature")
                Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                    Text("Request permissions again")
                }
            }

            else -> {
                Text("Permissions permanently denied. Enable them in settings")
                Button(onClick = { openAppSettings(context) }) {
                    Text("Open Settings")
                }
            }
        }
    }
}
private fun openAppSettings(context: Context) {
    val intent = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}

private fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
}

class Classifier(private val context: Context) {
    private var model: Interpreter? = null
    private var labels: List<String> = listOf()
    private val modelLock = Any()

    init {
        try {
            model = Interpreter(loadModelFile(), Interpreter.Options())
            labels = loadLabels()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadModelFile(): ByteBuffer {
        return FileUtil.loadMappedFile(context, "resnet50.tflite")
    }

    private fun loadLabels(): List<String> {
        return context.assets.open("labels.txt").bufferedReader().useLines { lines ->
            lines.map {
                it.split(',')[0].trim()
            }.toList()
        }
    }

    fun classifyImage(bitmap: Bitmap): String = synchronized(modelLock) {
        val input = preprocessImage(bitmap)
        val output = Array(1) { FloatArray(labels.size) }
        model?.run(input, output)
        output[0].indices.maxByOrNull { output[0][it] }?.let { labels[it] } ?: "Unknown"
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val byteBuffer = ByteBuffer.allocateDirect(224 * 224 * 3 * 4).apply {
            order(ByteOrder.nativeOrder())
            val intValues = IntArray(224 * 224)
            resizedBitmap.getPixels(intValues, 0, 224, 0, 0, 224, 224)

            var pixel = 0
            for (i in 0 until 224) {
                for (j in 0 until 224) {
                    val value = intValues[pixel++]
                    putFloat(((value shr 16) and 0xFF) / 255.0f)
                    putFloat(((value shr 8) and 0xFF) / 255.0f)
                    putFloat((value and 0xFF) / 255.0f)
                }
            }
            rewind()
        }
        return byteBuffer
    }

    fun close() = synchronized(modelLock) {
        model?.close()
        model = null
    }
}
