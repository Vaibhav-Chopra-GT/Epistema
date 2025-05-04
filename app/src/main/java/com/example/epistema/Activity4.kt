package com.example.epistema

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.registerForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.example.epistema.ui.LocationsScreen
import com.example.epistema.ui.ProfileScreen
import com.example.epistema.ui.theme.EpistemaTheme
import com.google.android.gms.location.LocationServices

class Activity4 : ComponentActivity() {
    private lateinit var fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient
    private var locationText by mutableStateOf("")

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fetchLocation { lat, lon ->
                locationText = "Latitude: $lat\nLongitude: $lon"
            }
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            EpistemaTheme {
                AppScaffold(selectedIndex = 3) { innerPadding ->
                    LocationsScreen(
                        modifier = Modifier.padding(innerPadding),
                        locationText = locationText,
                        onFetchLocation = {
                            checkLocationServicesAndPermission {
                                fetchLocation { lat, lon ->
                                    locationText = "Latitude: $lat\nLongitude: $lon"
                                }
                            }
                        },
                        onShowArticle = { article ->
                            val app = this.applicationContext as EpistemaApp

                            app.globalStateViewModel.setCurrentPage(article.pageid)
                            val intent = Intent(this, Activity3::class.java)
                            this.startActivity(intent)
                        }

                    )
                }
            }
        }
    }

    private fun checkLocationServicesAndPermission(onSuccess: () -> Unit) {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isGpsEnabled) {
            Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        } else {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PermissionChecker.PERMISSION_GRANTED

            if (hasPermission) {
                onSuccess()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun fetchLocation(onResult: (Double, Double) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PermissionChecker.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onResult(location.latitude, location.longitude)
                } else {
                    Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
