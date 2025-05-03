package com.example.epistema.ui
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.epistema.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
//import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class WikipediaViewModel : ViewModel() {
    //    private val repository = WikipediaRepository()
    var isLoading by mutableStateOf(false)
        private set

    var articles by mutableStateOf<List<Article>>(emptyList())
        private set

    fun fetchArticles(category: String) {
        viewModelScope.launch {
            isLoading = true
            articles = emptyList()

            try {
                val collectedArticles = mutableListOf<Article>()

                while (collectedArticles.size < 20) {
                    // Step 1: Fetch subcategories
                    val subcatUrl =
                        "https://en.wikipedia.org/w/api.php?action=query&format=json&list=categorymembers&cmtitle=Category:$category&cmlimit=50&cmtype=subcat"

                    val subcatResponse = withContext(Dispatchers.IO) {
                        URL(subcatUrl).readText()
                    }

                    val subcatJson = JSONObject(subcatResponse)
                    val subcats = subcatJson.getJSONObject("query").getJSONArray("categorymembers")

                    val chosenCategory = if (subcats.length() > 0) {
                        val subcatTitles = (0 until subcats.length()).map {
                            subcats.getJSONObject(it).getString("title").removePrefix("Category:")
                        }
                        subcatTitles.random()
                    } else {
                        category
                    }

                    // Step 2: Fetch up to 100 articles from chosenCategory
                    val pageUrl =
                        "https://en.wikipedia.org/w/api.php?action=query&format=json&list=categorymembers&cmtitle=Category:$chosenCategory&cmlimit=100&cmtype=page"

                    val pageResponse = withContext(Dispatchers.IO) {
                        URL(pageUrl).readText()
                    }

                    val pageJson = JSONObject(pageResponse)
                    val pages = pageJson.getJSONObject("query").getJSONArray("categorymembers")
                    val shuffledPages = (0 until pages.length()).shuffled()

                    // Step 3: Concurrently fetch image URLs for the articles
                    coroutineScope {
                        val jobs = mutableListOf<Deferred<Unit>>()

                        for (index in shuffledPages) {
                            if (collectedArticles.size >= 20) break

                            val job = async {
                                if (collectedArticles.size >= 20) return@async

                                val page = pages.getJSONObject(index)
                                val pageId = page.getInt("pageid")
                                val title = page.getString("title")
                                val imageUrl = fetchArticleImage(title)

                                if (imageUrl != null) {
                                    synchronized(collectedArticles) {
                                        if (collectedArticles.size < 20) {
                                            collectedArticles.add(Article(pageId, title, imageUrl))
                                        }
                                    }
                                }
                            }
                            jobs.add(job)
                        }

                        jobs.awaitAll()
                    }

                    // If fewer than 20, repeat with another subcategory
                }

                withContext(Dispatchers.Main) {
                    articles = collectedArticles
                    isLoading = false
                }

            } catch (e: Exception) {
                Log.e("WikipediaViewModel", "Error fetching articles", e)
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }









    private suspend fun fetchArticleImage(title: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val encodedTitle = URLEncoder.encode(title, "UTF-8")
                val url =
                    "https://en.wikipedia.org/w/api.php?action=query&titles=$encodedTitle&prop=pageimages&format=json&pithumbsize=300"

                val response = URL(url).readText()
                val pages = JSONObject(response).getJSONObject("query").getJSONObject("pages")
                val firstPage = pages.keys().asSequence().firstOrNull()?.let { pages.getJSONObject(it) }

                firstPage?.getJSONObject("thumbnail")?.getString("source")
            } catch (e: Exception) {
                null
            }
        }
    }


}
