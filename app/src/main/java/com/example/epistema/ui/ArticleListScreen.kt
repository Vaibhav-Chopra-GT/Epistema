import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

import android.util.Log
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.epistema.Activity3
import com.example.epistema.Activity6
import com.example.epistema.Article
import com.example.epistema.ui.WikipediaViewModel

@Composable
fun ArticleListScreen(
    modifier: Modifier = Modifier,
    section: String,
    viewModel: WikipediaViewModel = viewModel()
) {
    LaunchedEffect(section) {
        viewModel.fetchArticles(section)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Row with Title and Settings Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Articles in $section",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f) // Allows text to take available space
            )

            Button(
                onClick = {  },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Settings")
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(viewModel.articles) { article ->
                ArticleItem(article)
            }
        }
    }
}



@Composable
fun ArticleItem(article: Article) {
    val defaultImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6a/Science-symbol-2.svg/512px-Science-symbol-2.svg.png"
    val imageUrl = article.imageUrl ?: defaultImageUrl
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                context.startActivity(Intent(context, Activity3::class.java))
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Science Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(165.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = article.title,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}