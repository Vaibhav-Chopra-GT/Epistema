package com.example.epistema.ui
import androidx.compose.material.icons.filled.Place
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
//import androidx.compose.material.icons.filled.FilterAlt

//import androidx.compose.material.icons.filled.FilterList

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun LocationsScreen(modifier: Modifier = Modifier) {  // Added modifier parameter
    Column(modifier = modifier.fillMaxSize()) {  // Applied modifier
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Places",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { /* No functionality */ }) {
                Icon(Icons.Default.FilterAlt, contentDescription = "Filter")
            }
        }

        // Search Bar (UI only)
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text("Search Places") },
            shape = RoundedCornerShape(16.dp),
            enabled = false
        )

        // Places List (Dummy Data)
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            items(dummyPlaces) { place ->
                PlaceItem(place)
            }
        }
    }
}

@Composable
fun PlaceItem(place: Place) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = place.imageRes),
            contentDescription = place.name,
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = place.name, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text(text = place.description, fontSize = 14.sp, color = Color.Gray)
            Text(
                text = "${place.distance} km",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// Dummy data model
data class Place(val name: String, val description: String, val distance: Double, val imageRes: Int)

// Sample data
val dummyPlaces = listOf(
    Place("Battle of Delhi (1803)", "Part of the Second Anglo-Maratha War", 0.54, R.drawable.place4),
    Place("Sarai Kale Khan Nizamuddin metro station", "Metro station in Delhi, India", 4.03, R.drawable.place2),
    Place("New Ashok Nagar metro station", "Metro station in Delhi, India", 3.20, R.drawable.place3),
    Place("Anand Vihar metro station", "Metro station in Delhi, India", 4.58, R.drawable.place1),
    Place("IP Extension metro station", "Metro station in Delhi, India", 3.04, R.drawable.place5)
)
