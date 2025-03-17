package com.example.epistema.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ArticleDetailScreen(articleTitle: String, navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = articleTitle,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = "This is a article about $articleTitle. Science helps us understand the world better by providing a systematic \n" +
                    "approach to discovering truths about the natural world. \n" +
                    "\n" +
                    "The foundation of science lies in observation, experimentation, and logical reasoning. \n" +
                    "Through the scientific method, researchers develop hypotheses, conduct experiments, \n" +
                    "and analyze data to draw meaningful conclusions. This process has led to groundbreaking \n" +
                    "discoveries in fields such as physics, chemistry, biology, and astronomy.\n" +
                    "\n" +
                    "For example, in physics, Einstein’s theory of relativity reshaped our understanding \n" +
                    "of space and time. In biology, the discovery of DNA revolutionized genetics and medicine, \n" +
                    "paving the way for advancements like gene therapy and personalized medicine. \n" +
                    "Astronomy has given us insights into the vastness of the universe, \n" +
                    " from black holes to exoplanets that might harbor life.\n" +
                    "\n" +
                    "Science also plays a crucial role in tackling global challenges. \n" +
                    "Climate science helps us understand and address climate change, \n" +
                    "while medical research has led to vaccines and treatments for various diseases. \n" +
                    "Technological innovations driven by scientific research have improved communication, \n" +
                    "transportation, and energy efficiency.\n" +
                    "\n" +
                    "As we continue to explore the mysteries of the universe, science remains \n" +
                    "a powerful tool for human progress. It encourages curiosity, critical thinking, \n" +
                    "and innovation—qualities essential for shaping a better future.\"",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )

        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Back to Articles")
        }
    }
}
