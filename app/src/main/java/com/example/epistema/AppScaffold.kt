package com.example.epistema

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Bookmark

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun AppScaffold(
    selectedIndex: Int,
    content: @Composable (PaddingValues) -> Unit
) {
    val context = LocalContext.current
    val screens = listOf("Home", "Search", "Profile", "Location", "Saved")

    Scaffold(
        bottomBar = {
            NavigationBar {
                screens.forEachIndexed { index, label ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Filled.Home
                                    1 -> Icons.Filled.Search
                                    3 -> Icons.Filled.Place
                                    4 -> Icons.Filled.Bookmark
                                    else -> Icons.Filled.Person
                                },
                                contentDescription = label
                            )
                        },
                        label = { Text(label) },
                        selected = index == selectedIndex,
                        onClick = {
                            if (index != selectedIndex) {
                                when (index) {
                                    0 -> context.startActivity(Intent(context, MainActivity::class.java))
                                    1 -> context.startActivity(Intent(context, Activity2::class.java))
                                    2 -> context.startActivity(Intent(context, Activity3::class.java))
                                    3 -> context.startActivity(Intent(context, Activity4::class.java))
                                    4 -> context.startActivity(Intent(context, Activity5::class.java))

                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
