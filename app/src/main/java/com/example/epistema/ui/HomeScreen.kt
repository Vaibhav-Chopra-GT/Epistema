package com.example.epistema.ui
import com.example.epistema.viewmodels.ArticleOfTheDayManager
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar
import androidx.annotation.RequiresApi
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
import com.example.epistema.EpistemaApp
import com.example.epistema.R
import com.example.epistema.data.WikiSearchResult
import com.example.epistema.viewmodels.SearchViewModel
import kotlinx.coroutines.CoroutineScope
import org.jsoup.Jsoup
//
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

import kotlinx.coroutines.withContext
//import org.jsoup.Jsoup
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

import org.json.JSONObject
import com.example.epistema.TrendingPage
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    initialQuery: String = "",
    onStartVoiceRecognition: ((String) -> Unit) -> Unit
) {
    val viewModel: SearchViewModel = viewModel()
    var searchQuery by remember { mutableStateOf(initialQuery) }
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
                        val app = context.applicationContext as EpistemaApp

                        app.globalStateViewModel.setCurrentPage(result.pageId)
                        val intent = Intent(context, Activity3::class.java)
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

@RequiresApi(Build.VERSION_CODES.O)
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
            val context = LocalContext.current
            var articleTitle by remember { mutableStateOf<String?>(null) }
            var isLoading by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                articleTitle = ArticleOfTheDayManager.getTodayArticle(context)?.first
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp)
                    .clickable(enabled = !isLoading) {
                        val saved = ArticleOfTheDayManager.getTodayArticle(context)
                        if (saved != null) {
                            val app = context.applicationContext as EpistemaApp

                            app.globalStateViewModel.setCurrentPage(saved.second)
                            val intent = Intent(context, Activity3::class.java)
                            context.startActivity(intent)
                        } else {
                            isLoading = true
                            // Launch coroutine from outside Composable scope
                            CoroutineScope(Dispatchers.IO).launch {
                                val randomArticle = fetchRandomArticle()
                                if (randomArticle != null) {
                                    ArticleOfTheDayManager.saveTodayArticle(context, randomArticle.first, randomArticle.second)
                                    withContext(Dispatchers.Main) {
                                        val app = context.applicationContext as EpistemaApp

                                        app.globalStateViewModel.setCurrentPage(randomArticle.second)
                                        val intent = Intent(context, Activity3::class.java)
                                        context.startActivity(intent)
                                        articleTitle = randomArticle.first
                                        isLoading = false
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        isLoading = false
                                    }
                                }
                            }
                        }
                    }
                ,
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Article of the day - $articleTitle" ?: if (isLoading) "Loading..." else "Article of the day",
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
                val leftCategory = categories[index * 2]
                val rightCategory = categories[index * 2 + 1]

                CategoryCard(
                    category = leftCategory,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val intent = Intent(context, Activity6::class.java)
                        intent.putExtra("CATEGORY", leftCategory)
                        context.startActivity(intent)
                    }
                )
                CategoryCard(
                    category = rightCategory,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val intent = Intent(context, Activity6::class.java)
                        intent.putExtra("CATEGORY", rightCategory)
                        context.startActivity(intent)
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

        item {
            var trendingPages by remember { mutableStateOf<List<TrendingPage>>(emptyList()) }
            var loading by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                trendingPages = fetchTrendingPages()
                loading = false
            }

            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column {
                    trendingPages.forEachIndexed { index, page ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val app = context.applicationContext as EpistemaApp

                                    app.globalStateViewModel.setCurrentPage(page.pageId)
                                    val intent = Intent(context, Activity3::class.java)
                                    context.startActivity(intent)
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.width(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = page.title.replace('_', ' '),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${page.views} views",
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        Divider(thickness = 0.5.dp, color = Color.LightGray)
                    }
                }
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

suspend fun fetchRandomArticle(): Pair<String, Int>? {
    return try {
        val url = "https://en.wikipedia.org/w/api.php?action=query&format=json&list=random&rnlimit=1&rnnamespace=0"
        val response = Jsoup.connect(url).ignoreContentType(true).execute().body()
        val json = org.json.JSONObject(response)
        val random = json.getJSONObject("query").getJSONArray("random").getJSONObject(0)
        val title = random.getString("title")
        val pageId = random.getInt("id")
        title to pageId
    } catch (e: Exception) {
        null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private suspend fun fetchTrendingPages(): List<TrendingPage> = withContext(Dispatchers.IO) {
    val trendingPages = mutableListOf<TrendingPage>()
    try {
        // Get today's date in YYYY/MM/DD format
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -3)  // Subtract 3 days
        val threeDaysAgo = calendar.time

        // Format the date in YYYY/MM/DD format
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val formattedDate = dateFormat.format(threeDaysAgo)

        // Construct the URL using the formatted date
        val url = "https://wikimedia.org/api/rest_v1/metrics/pageviews/top/en.wikipedia/all-access/$formattedDate"

        // Fetch the data from the API
        val response = Jsoup.connect(url).ignoreContentType(true).execute().body()
        val json = JSONObject(response)

        // Extract articles from the response
        val itemsArray = json.getJSONArray("items")
        if (itemsArray.length() > 0) {
            val articles = itemsArray.getJSONObject(0).getJSONArray("articles")

            // Iterate over the articles and get their details
            for (i in 0 until minOf(10, articles.length())) {
                val article = articles.getJSONObject(i)
                val title = article.getString("article")
                val views = article.getInt("views")

                // Optional: you can skip non-article entries like "Main_Page"
                if (title != "Main_Page" && !title.startsWith("Special:")) {
                    val pageId = getPageIdFromTitle(title)

                    trendingPages.add(
                        TrendingPage(
                            title.replace("_", " "),
                            views,
                            pageId
                        )
                    )
                }
            }
        }
    } catch (e: Exception) {
        Log.e("Trending", "Error fetching trending pages: ${e.message}", e)
    }
    return@withContext trendingPages
}



fun getPageIdFromTitle(title: String): Int {
    val url = "https://en.wikipedia.org/w/api.php?action=query&format=json&titles=$title"
    val connection = URL(url).openConnection() as HttpURLConnection
    connection.requestMethod = "GET"

    val reader = BufferedReader(InputStreamReader(connection.inputStream))
    val response = reader.readText()
    reader.close()

    val json = JSONObject(response)
    val pages = json.getJSONObject("query").getJSONObject("pages")
    val pageId = pages.keys().asSequence().first().toInt()

    return pageId
}

