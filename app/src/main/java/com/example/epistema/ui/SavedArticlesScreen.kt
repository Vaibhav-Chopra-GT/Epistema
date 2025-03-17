package com.example.epistema.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.epistema.R

@Composable
fun SavedArticlesScreen(modifier: Modifier = Modifier) {
    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) } // 0 for "All articles", 1 for "Reading lists"

    Column(modifier = modifier.fillMaxSize()) { // ✅ Use modifier here
        // Title
        Text(
            text = "Saved",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        // Search Bar
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Search saved articles") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TabItem(title = "All articles", isSelected = selectedTab == 0, onClick = { selectedTab = 0 },
                modifier = Modifier.weight(1f) // ✅ Apply weight here
            )
            TabItem(title = "Reading lists", isSelected = selectedTab == 1, onClick = { selectedTab = 1 },
                modifier = Modifier.weight(1f) // ✅ Apply weight here
            )
        }

        // Saved Articles List
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            items(dummyArticles) { article ->
                ArticleItem(article)
            }
        }
    }
}


@Composable
fun TabItem(title: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color.Gray else Color.LightGray
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxWidth() // Remove weight from here
    ) {
        Text(text = title, color = Color.Black)
    }
}



@Composable
fun ArticleItem(article: Article) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = article.imageRes),
            contentDescription = article.title,
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = article.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = article.description, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

// Dummy Data Model
data class Article(val title: String, val description: String, val imageRes: Int)

// Sample Data
val dummyArticles = listOf(
//    Article("Kočani nightclub fire", "Nightclub fire in North Macedonia", R.drawable.article1),
    Article("Geography of Ireland", "Ireland is an island in Northern Europe", R.drawable.article2),
//    Article("Artificial Intelligence", "Exploring the impact of AI on society", R.drawable.article3),
//    Article("Mars Exploration", "NASA’s latest mission to the red planet", R.drawable.article4)
)

