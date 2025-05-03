package com.example.epistema.ui

import android.content.Intent
import android.text.Html
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.epistema.Activity3
import com.example.epistema.EpistemaApp
import com.example.epistema.data.SavedArticle
import com.example.epistema.viewmodels.SavedArticlesViewModel
import org.jsoup.Jsoup

enum class SortOrder { NEWEST, OLDEST }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedArticlesScreen(
    modifier: Modifier = Modifier,
    savedVm: SavedArticlesViewModel = viewModel()
) {
    var sortOrder by remember { mutableStateOf(SortOrder.NEWEST) }
    var searchText by remember { mutableStateOf("") }
    var showSortDialog by remember { mutableStateOf(false) }
    val allSaved by savedVm.savedArticles.collectAsState()

    val filtered = remember(allSaved, searchText, sortOrder) {
        allSaved
            .filter { it.title.contains(searchText, ignoreCase = true) }
            .sortedBy { if (sortOrder == SortOrder.NEWEST) -it.id else it.id }
    }

    Surface(modifier = modifier.fillMaxSize()) {
        Column {
            Text(
                text = "Saved Articles",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    decorationBox = { inner ->
                        if (searchText.isEmpty()) {
                            Text(
                                "Search",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        inner()
                    }
                )
                IconButton(onClick = { showSortDialog = true }) {
                    Icon(Icons.Filled.Sort, contentDescription = "Sort")
                }
            }
            Divider()

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filtered) { article ->
                    SavedArticleItem(article = article,
                        onDelete = { savedVm.deleteArticle(article)})
                    Divider()
                }
            }
        }

        if (showSortDialog) {
            SortOrderDialog(
                current = sortOrder,
                onSortOrderSelected = {
                    sortOrder = it
                    showSortDialog = false
                },
                onDismiss = { showSortDialog = false }
            )
        }
    }
}

@Composable
private fun SavedArticleItem(
    article: SavedArticle,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    // strip HTML tags for preview
    val plain = remember(article.content) {
        Jsoup.parse(article.content).text() // Extract text without HTML
    }
    val preview = plain
        .take(150)
        .trimEnd()
        .let { if (plain.length > 150) "$it…" else it }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val app = context.applicationContext as EpistemaApp

                app.globalStateViewModel.setCurrentArticle(article.id)
                val intent = Intent(context, Activity3::class.java)
                context.startActivity(intent)
            }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(
                text = article.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = preview,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete article",
                tint = Color.Black
            )
        }
    }
}

@Composable
private fun SortOrderDialog(
    current: SortOrder,
    onSortOrderSelected: (SortOrder) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sort by") },
        text = {
            Column {
                RadioButtonWithLabel(
                    label = "Newest first",
                    selected = current == SortOrder.NEWEST,
                    onClick = { onSortOrderSelected(SortOrder.NEWEST) }
                )
                RadioButtonWithLabel(
                    label = "Oldest first",
                    selected = current == SortOrder.OLDEST,
                    onClick = { onSortOrderSelected(SortOrder.OLDEST) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

@Composable
private fun RadioButtonWithLabel(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
    }
}
