package com.example.epistema.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.epistema.Activity3
import com.example.epistema.network.RetrofitInstance
import com.example.epistema.data.SummaryResponse
import com.example.epistema.viewmodels.HistoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val filterOptions = listOf("Recent", "A-Z", "Z-A")
    var selectedFilter by remember { mutableStateOf(filterOptions.first()) }

    // ViewModel and state
    val viewModel: HistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val historyList by viewModel.historyFlow.collectAsState()

    // Apply filter/sort
    val displayedList = remember(historyList, selectedFilter) {
        when (selectedFilter) {
            "A-Z" -> historyList.sortedBy { it.pageId } // or .sortedBy { it.title } if metadata stored
            "Z-A" -> historyList.sortedByDescending { it.pageId }
            else -> historyList
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Clear history & filter row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { viewModel.clearHistory(); }) {
                Text("Clear History")
            }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedFilter,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sort") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().width(120.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    filterOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedFilter = option
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // History list
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(displayedList) { entry ->
                HistoryItemRow(
                    pageId = entry.pageId,
                    onClick = {
                        val intent = Intent(context, Activity3::class.java)
                        intent.putExtra("PAGE_ID", entry.pageId)
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
private fun HistoryItemRow(
    pageId: Int,
    onClick: () -> Unit
) {
    // Fetch title & thumbnail dynamically
    val summary by produceState<SummaryResponse?>(initialValue = null, key1 = pageId) {
        value = withContext(Dispatchers.IO) {
            val parseResp = RetrofitInstance.api.parseArticleById(
                pageid = pageId.toString()
            )
            val title = parseResp.parse.title
            RetrofitInstance.api.getSummary(title)
        }
    }

    val displayTitle = summary?.title ?: "Loading..."
    val imageUrl = summary?.thumbnail?.source

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = displayTitle,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp)
            )
            Text(
                text = displayTitle,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
