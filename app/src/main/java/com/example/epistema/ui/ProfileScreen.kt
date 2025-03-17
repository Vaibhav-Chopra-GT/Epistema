package com.example.epistema.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.epistema.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    var isSidebarVisible by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()
    // Define sections for the demo
    val sections = listOf(
        "Section 1", "Section 2", "Section 3", "Section 4",
        "Section 5", "Section 6", "Section 7", "Section 8",
        "Section 9", "Section 10", "Section 11", "Section 12"
    )
    val coroutineScope = rememberCoroutineScope()

    var isGraphicalSidebarVisible by remember { mutableStateOf(false) }

    // Store the vertical offset for each section.
    val sectionOffsets = remember { mutableStateListOf<Float>() }
    if (sectionOffsets.size != sections.size) {
        sectionOffsets.clear()
        repeat(sections.size) { sectionOffsets.add(0f) }
    }

    val currentSectionIndex by remember {
        derivedStateOf {
            if (sectionOffsets.isNotEmpty()) {
                val currentScroll = scrollState.value.toFloat()
                sectionOffsets.withIndex().minByOrNull { abs(it.value - currentScroll) }?.index ?: 0
            } else 0
        }
    }

    LaunchedEffect(scrollState.value) {
        snapshotFlow { scrollState.value }
            .distinctUntilChanged()
            .collectLatest {
                isGraphicalSidebarVisible = true
                delay(2000)
                isGraphicalSidebarVisible = false
            }
    }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar()
            Box(modifier = Modifier.fillMaxSize()) {
                // Main content row: article and TOC sidebar.
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Article content with vertical scroll.
                    Column(
                        modifier = Modifier
                            .weight(3f)
                            .verticalScroll(scrollState)
                            .padding(8.dp)
                    ) {
                        ArticleImage()
                        ArticleTitle()
                        ArticleText(sections = sections, sectionOffsets = sectionOffsets)
                    }
                    // Animated Table of Contents sidebar.
                    AnimatedVisibility(
                        visible = isSidebarVisible,
                        enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                            initialOffsetX = { it },
                            animationSpec = tween(durationMillis = 300)
                        ),
                        exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                            targetOffsetX = { it },
                            animationSpec = tween(durationMillis = 300)
                        )
                    ) {
                        TableOfContents(
                            scrollState = scrollState,
                            sections = sections,
                            sectionOffsets = sectionOffsets
                        )
                    }
                }
                IconButton(
                    onClick = { isSidebarVisible = !isSidebarVisible },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(
                            color = if (isSidebarVisible) Color.Transparent else Color(0x80000000), // Semi-transparent black when closed
                            shape = MaterialTheme.shapes.medium
                        )
                        .clip(MaterialTheme.shapes.medium)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Toggle Sidebar",
                        tint = if (isSidebarVisible) Color.DarkGray else Color.White // Dark gray when open, white when closed
                    )
                }




            }
        }
    }
}

@Composable
fun SearchBar() {
    OutlinedTextField(
        value = TextFieldValue(""),
        onValueChange = {},
        label = { Text("Search") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    )
}

@Composable
fun ArticleImage() {
    Image(
        painter = painterResource(id = R.drawable.img),
        contentDescription = "Article Image",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(MaterialTheme.shapes.medium)
            .padding(bottom = 16.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ArticleTitle() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = "Article Title",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { /* Implement Text-to-Speech */ }) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Text to Speech",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
fun ArticleText(
    sections: List<String>,
    sectionOffsets: MutableList<Float>
) {
    val sectionContent = sections.associateWith { section ->
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris " +
                "nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in " +
                "reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia " +
                "deserunt mollit anim id est laborum."
    }

    sections.forEachIndexed { index, title ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    sectionOffsets[index] = coordinates.positionInParent().y
                }
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .semantics { contentDescription = title }
            )
            Text(
                text = sectionContent[title] ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


@Composable
fun TableOfContents(
    scrollState: ScrollState,
    sections: List<String>,
    sectionOffsets: List<Float>
) {
    val coroutineScope = rememberCoroutineScope()
    Surface(
        modifier = Modifier
            .width(200.dp)
            .fillMaxHeight()
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                .padding(16.dp)
        ) {
            Text(
                text = "Table of Contents",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))
            sections.forEachIndexed { index, section ->
                Text(
                    text = section,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (index == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable {
                            coroutineScope.launch {
                                val target = sectionOffsets.getOrNull(index) ?: 0f
                                scrollState.animateScrollTo(target.toInt())
                            }
                        }
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    ProfileScreen()
}
