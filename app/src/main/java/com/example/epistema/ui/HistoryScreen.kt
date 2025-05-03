package com.example.epistema.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.epistema.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    // Filter options that resemble a YouTube filter bar
    val filterOptions = listOf(
        "All",
        "Viewed Last Week",
        "Viewed Last Month",
        "Trending",
        "Most Viewed",
        "Recently Added",
        "A-Z",
        "Z-A"
    )
    var selectedFilter by remember { mutableStateOf(filterOptions.first()) }

    // Sample list of visited Wikipedia articles with associated image resources
    val visitedArticles = listOf(
        "Article 1: The Renaissance" to R.drawable.img,
        "Article 2: World War II" to R.drawable.img,
        "Article 3: The Cold War" to R.drawable.img,
        "Article 4: Space Exploration" to R.drawable.img
    )

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search articles") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Dropdown similar to YouTube's filter bar
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedFilter,
                onValueChange = { },
                readOnly = true,
                label = { Text("Filter") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier.menuAnchor() // required modifier for ExposedDropdownMenuBox
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                filterOptions.forEach { filterOption ->
                    DropdownMenuItem(
                        text = { Text(filterOption) },
                        onClick = {
                            selectedFilter = filterOption
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of visited articles (you can add filtering logic based on searchQuery and selectedFilter)
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(visitedArticles) { (article, imageRes) ->
                Card(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = article,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(end = 16.dp)
                        )
                        Text(
                            text = article,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}