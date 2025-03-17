package com.example.epistema.ui

import android.R.attr.thickness
import android.content.Intent

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.epistema.Activity6
import androidx.compose.ui.platform.LocalContext
import com.example.epistema.MainActivity
import com.example.epistema.R

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current
    val categories = listOf<String>("Art", "History", "Geography", "Science", "Politics", "Mathematics", "Literature", "Philosophy")
    Text(text = "Epistema", fontSize = 26.sp, modifier = Modifier.fillMaxSize().offset(25.dp,45.dp), fontWeight = FontWeight.Bold)
    Row(modifier = Modifier.fillMaxWidth().offset(x = 25.dp,y = 80.dp)){
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Search") },
            singleLine = true,
            shape = RoundedCornerShape(20.dp)
        )
        Spacer(modifier = Modifier.width(25.dp))
        Image(
            painter = painterResource(id = R.drawable.mic_img),
            contentDescription = "search by voice",
            modifier = Modifier
                .size(25.dp)
                .clickable {}
                .offset(y = 20.dp)
        )
    }
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp).navigationBarsPadding().offset(0.dp,150.dp)){
        items(1){
            Card(
                modifier = Modifier.fillMaxSize().padding(16.dp).height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("Article of the day", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        items(4){ index ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(150.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(categories[index*2], fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(150.dp)
                        .clickable {
                            if (categories[index*2+1] == "Science") {
                                context.startActivity(Intent(context, Activity6::class.java))
                            }
                        },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(categories[index*2 + 1], fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        items(1){
            Text(text = "Trending pages", fontWeight = FontWeight.Bold, color = Color.DarkGray)
            HorizontalDivider(thickness = 1.dp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(15.dp))
        }
        items(5){ index ->
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 0.dp)){
                Text(text = "${index+1}. Trending page ${index+1}")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Views : ${(1000/(index+1))*23}")
            }
        }
        items(1){
            Spacer(modifier = Modifier.height(250.dp))
            Text(text = "end of page")
        }

    }
}
