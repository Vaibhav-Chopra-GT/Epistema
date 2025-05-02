package com.example.epistema.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.epistema.Activity3
import com.example.epistema.Activity6
import com.example.epistema.R
import com.example.epistema.data.WikiSearchResult
import com.example.epistema.viewmodels.SearchViewModel
import org.jsoup.Jsoup

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onStartVoiceRecognition: ((String) -> Unit) -> Unit
) {
    val viewModel: SearchViewModel = viewModel()
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            viewModel.searchWikipedia(searchQuery)
        } else {
            viewModel.clearSearchResults()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onVoiceInput = { voiceText -> searchQuery = voiceText },
            onStartVoiceRecognition = onStartVoiceRecognition,
            modifier = Modifier.padding(16.dp)
        )

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            searchQuery.isNotEmpty() && searchResults.isNotEmpty() -> {
                SearchResultsList(
                    results = searchResults,
                    onItemClick = { result ->
                        val intent = Intent(context, Activity3::class.java).apply {
                            putExtra("PAGE_ID", result.pageId)
                        }
                        context.startActivity(intent)
                    }
                )
            }
            searchQuery.isNotEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No results found", color = Color.Gray)
                }
            }
            else -> {
                OriginalContent()
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onVoiceInput: (String) -> Unit,
    onStartVoiceRecognition: ((String) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Search Wikipedia") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.mic_img),
                    contentDescription = "Voice search",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            onStartVoiceRecognition { voiceText ->
                                onVoiceInput(voiceText)
                            }
                        }
                )
            }
        )
    }
}

@Composable
private fun SearchResultsList(
    results: List<WikiSearchResult>,
    onItemClick: (WikiSearchResult) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(results) { result ->
            SearchResultItem(
                title = result.title,
                snippet = result.snippet.stripHtmlTags(),
                onClick = { onItemClick(result) }
            )
            Divider(color = Color.LightGray, thickness = 1.dp)
        }
    }
}

@Composable
private fun SearchResultItem(title: String, snippet: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = snippet, fontSize = 14.sp, color = Color.Gray)
    }
}
@Composable
private fun OriginalContent() {
    val context = LocalContext.current
    val categories = listOf(
        "Art", "History", "Geography", "Science",
        "Politics", "Mathematics", "Literature", "Philosophy"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 25.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        "Article of the day",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(4) { index ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CategoryCard(
                    category = categories[index * 2],
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (categories[index * 2] == "Science") {
                            context.startActivity(Intent(context, Activity6::class.java))
                        }
                    }
                )
                CategoryCard(
                    category = categories[index * 2 + 1],
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (categories[index * 2 + 1] == "Science") {
                            context.startActivity(Intent(context, Activity6::class.java))
                        }
                    }
                )
            }
        }

        item {
            Text(
                text = "Trending pages",
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp)
            )
            Divider(thickness = 1.dp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(15.dp))
        }

        items(5) { index ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(text = "${index + 1}. Trending page ${index + 1}")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Views: ${(1000 / (index + 1)) * 23}")
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(150.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(category, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private fun String.stripHtmlTags(): String = Jsoup.parse(this).text()