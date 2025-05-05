package com.example.epistema.ui

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.epistema.viewmodels.SaveState
import com.example.epistema.viewmodels.SavedArticlesViewModel
import com.example.epistema.viewmodels.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.io.File
import java.util.Locale
import kotlin.math.roundToInt

private const val TAG = "ProfileScreen"

sealed class ContentItem {
    data class Header(val level: Int, val text: String) : ContentItem()
    data class Paragraph(val element: Element) : ContentItem()
    data class ListBullet(val element: Element) : ContentItem()
    data class Image(
        val url: String,
        val caption: String?,
        val classes: Set<String>,
        val widthAttr: Int?,
        val heightAttr: Int?
    ) : ContentItem()
    data class Gallery(val images: List<Image>) : ContentItem()
    data class Table(val headers: List<String>, val rows: List<List<Element>>) : ContentItem()
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = viewModel(),
    savedVm: SavedArticlesViewModel = viewModel(),
    offlineTitle: String = "",
    offlineContent: String = "",
    onArticleClosed: () -> Unit
) {
    val article by viewModel.currentArticle.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isOffline = offlineContent.isNotBlank()

    // Decide title & raw HTML based on offline vs online
    val articleTitle = remember(isOffline, offlineTitle, article?.title) {
        if (isOffline) offlineTitle else article?.title.orEmpty()
    }
    val rawHtml = remember(isOffline, offlineContent, article?.content) {
        if (isOffline) offlineContent else article?.content.orEmpty()
    }

    var showTOC by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutine = rememberCoroutineScope()
    val saveState by savedVm.saveState.collectAsState()
    var showSaveDialog by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            onArticleClosed()
        }
    }
    LaunchedEffect(saveState) {
        when (saveState) {
            is SaveState.Success, is SaveState.Error -> {
                showSaveDialog = false
                delay(2000)
                savedVm.resetSaveState()
            }
            else -> {}
        }
    }


    // Parse HTML → contentItems
    val contentItems = remember(rawHtml) {
        val doc = try {
            Jsoup.parse(rawHtml)
        } catch (e: Exception) {
            Log.e(TAG, "HTML parsing failed", e)
            Jsoup.parse("") // Return empty document to avoid null
        }
        val elems = doc.select("h2, h3, p, ul, ol, img, div.trow, table.wikitable")
        val raw = mutableListOf<ContentItem>()
        for (el in elems) {
            try {
                when {
                    el.tagName() == "h2" ->
                        raw += ContentItem.Header(2, el.text().trim())

                    el.tagName() == "h3" ->
                        raw += ContentItem.Header(3, el.text().trim())

                    el.tagName() == "p" ->
                        raw += ContentItem.Paragraph(el)

                    el.tagName() in listOf("ul", "ol") ->
                        el.select("li").forEach { li ->
                            raw += ContentItem.ListBullet(li)
                        }

                    el.tagName() == "div" && el.hasClass("trow") -> {
                        val imgs = el.select("img").map { imgEl ->
                            val rawSrc = imgEl.attr("src")
                            val url = when {
                                rawSrc.startsWith("//") -> "https:$rawSrc"
                                rawSrc.startsWith("http") -> rawSrc
                                else -> "https:$rawSrc"
                            }
                            ContentItem.Image(
                                url = url,
                                caption = imgEl.attr("alt").takeIf(String::isNotBlank),
                                classes = imgEl.classNames(),
                                widthAttr = imgEl.attr("width").toIntOrNull(),
                                heightAttr = imgEl.attr("height").toIntOrNull()
                            )
                        }
                        if (imgs.isNotEmpty()) raw += ContentItem.Gallery(imgs)
                    }

                    el.tagName() == "img"
                            && el.parents().none { it.hasClass("trow") }
                            && el.parents()
                        .none { it.tagName() == "table" && it.hasClass("wikitable") } -> {
                        val rawSrc = el.attr("src")
                        val url = when {
                            rawSrc.startsWith("//") -> "https:$rawSrc"
                            rawSrc.startsWith("http") -> rawSrc
                            else -> "https:$rawSrc"
                        }
                        raw += ContentItem.Image(
                            url = url,
                            caption = el.attr("alt").takeIf(String::isNotBlank),
                            classes = el.classNames(),
                            widthAttr = el.attr("width").toIntOrNull(),
                            heightAttr = el.attr("height").toIntOrNull()
                        )
                    }

                    el.tagName() == "table" && el.hasClass("wikitable") -> {
                        val headers = el.select("thead tr th").map { it.text().trim() }
                        val rows = el.select("tbody tr").map { row -> row.select("td").toList() }
                        raw += ContentItem.Table(headers, rows)
                    }
                }
            }catch (e:Exception){
                Log.e(TAG, "Failed to process element: ${el.outerHtml()}", e);
            }
        }
        raw
    }

    // Build TOC entries for <h2>
    val tocEntries = remember(contentItems) {
        contentItems.mapIndexedNotNull { idx, item ->
            (item as? ContentItem.Header)
                ?.takeIf { it.level == 2 }
                ?.let { idx to it.text }
        }
    }

    val context = LocalContext.current
    val tts = remember {
        TextToSpeech(context) { /* no-op for now */ }
    }

    LaunchedEffect(tts) {
        if (tts.isLanguageAvailable(Locale.getDefault()) >= TextToSpeech.LANG_AVAILABLE) {
            tts.language = Locale.getDefault()
        }
    }

    DisposableEffect(tts) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    Surface(modifier = modifier.fillMaxSize()) {
        if (!isOffline && isLoading) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Surface
        }

        Box(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(3f)
                        .padding(12.dp)
                ) {
                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = articleTitle,
                                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                        if (!isOffline) {
                                                val textToRead = Jsoup.parse(rawHtml).text()
                                               tts.speak(
                                                       textToRead,
                                                      TextToSpeech.QUEUE_FLUSH,
                                                       null,
                                                         "articleReadAloud"
                                                            )
                                            }
                                   }) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Read Aloud")
                            }
                            IconButton(onClick = {
                                // Always allow saving from offline or online
                                if (isOffline) {
                                    Log.d(TAG, "Loading OFFLINE content: ${offlineContent.take(50)}...")
                                    savedVm.saveArticle(offlineTitle, offlineContent)
                                } else {
                                    Log.d(TAG, "Loading ONLINE content: ${article?.content?.take(50)}...")
                                    article?.let {
                                        savedVm.saveArticle(it.title, it.content)
                                    }
                                }
                                showSaveDialog = true
                            }) {
                                Icon(Icons.Default.Save, contentDescription = "Save Article")
                            }
                        }
                        Divider(color = Color.LightGray)
                    }

                    itemsIndexed(contentItems) { _, item ->
                        when (item) {
                            is ContentItem.Header -> {
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = item.text,
                                    style = if (item.level == 2)
                                        androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    else
                                        androidx.compose.material3.MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.onBackground

                                )
                            }
                            is ContentItem.Paragraph -> {
                                val annotated = buildAnnotatedString {
                                    item.element.childNodes().forEach { node ->
                                        when (node) {
                                            is TextNode -> append(node.text())
                                            is Element -> if (node.tagName() == "a") {
                                                val href = node.attr("href")
                                                val start = length
                                                append(node.text())
                                                addStyle(
                                                    SpanStyle(
                                                        color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                                        textDecoration = TextDecoration.Underline
                                                    ), start, length
                                                )
                                                addStringAnnotation(
                                                    tag = "URL",
                                                    annotation = href,
                                                    start = start,
                                                    end = length
                                                )
                                            } else append(
                                                node.text()
                                            )
                                            else -> append(node.outerHtml())
                                        }
                                    }
                                }
                                ClickableText(
                                    text = annotated,
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    style    = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onBackground
                                    ),
                                    onClick = { offset ->
                                        annotated.getStringAnnotations("URL", offset, offset)
                                            .firstOrNull()?.let { ann ->
                                                val href = ann.item
                                                val title = when {
                                                    href.startsWith("/wiki/") ->
                                                        href.removePrefix("/wiki/").replace('_', ' ')
                                                    href.contains("wikipedia.org/wiki/") ->
                                                        href.substringAfter("wikipedia.org/wiki/")
                                                            .substringBefore('?').replace('_', ' ')
                                                    else -> null
                                                }
                                                title?.let {
                                                    coroutine.launch {
                                                        viewModel.loadArticleByTitle(it)
                                                        listState.animateScrollToItem(0)
                                                    }
                                                }
                                            }
                                    }
                                )
                            }
                            is ContentItem.ListBullet -> {
                                val annotated = buildAnnotatedString {
                                    append("• ")
                                    item.element.childNodes().forEach { node ->
                                        when (node) {
                                            is TextNode -> append(node.text())
                                            is Element -> if (node.tagName() == "a") {
                                                val href = node.attr("href")
                                                val start = length
                                                append(node.text())
                                                addStyle(
                                                    SpanStyle(
                                                        color = MaterialTheme.colorScheme.onBackground,
                                                        textDecoration = TextDecoration.Underline
                                                    ), start, length
                                                )
                                                addStringAnnotation(
                                                    tag = "URL",
                                                    annotation = href,
                                                    start = start,
                                                    end = length
                                                )
                                            } else append(node.text())
                                            else -> append(node.outerHtml())
                                        }
                                    }
                                }
                                ClickableText(
                                    text = annotated,
                                    modifier = Modifier
                                        .padding(start = 16.dp, bottom = 2.dp)
                                        .fillMaxWidth(),
                                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                    onClick = { offset ->
                                        annotated.getStringAnnotations("URL", offset, offset)
                                            .firstOrNull()?.let { ann ->
                                                val href = ann.item
                                                val title = when {
                                                    href.startsWith("/wiki/") ->
                                                        href.removePrefix("/wiki/").replace('_', ' ')
                                                    href.contains("wikipedia.org/wiki/") ->
                                                        href.substringAfter("wikipedia.org/wiki/")
                                                            .substringBefore('?').replace('_', ' ')
                                                    else -> null
                                                }
                                                title?.let {
                                                    coroutine.launch {
                                                        viewModel.loadArticleByTitle(it)
                                                        listState.animateScrollToItem(0)
                                                    }
                                                }
                                            }
                                    }
                                )
                            }
                            is ContentItem.Image -> {
                                Spacer(Modifier.height(12.dp))
                                val isInfobox = item.classes.any { it.contains("infobox") }
                                val isThumb = item.classes.contains("thumb") || item.classes.contains("tmplt-image")
                                val isSvg = item.url.lowercase().endsWith(".svg") || "svg" in item.url
                                val isSmall = (item.widthAttr ?: 0) <= 64 && (item.heightAttr ?: 0) <= 64
                                val hasEmptyAlt = item.caption.isNullOrBlank()
                                val isLikelyIcon = isSmall && hasEmptyAlt && isSvg
                                val imgModifier = when {
                                    isLikelyIcon -> Modifier.size(24.dp)
                                    else -> {
                                        val base = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFEFEFEF))
                                        item.widthAttr?.let { w ->
                                            item.heightAttr?.let { h ->
                                                base.aspectRatio(w.toFloat() / h)
                                            }
                                        } ?: base
                                    }
                                }
                                val contentScale = when {
                                    isLikelyIcon -> ContentScale.Inside
                                    isThumb || isInfobox -> ContentScale.Crop
                                    else -> ContentScale.Fit
                                }
                                val context = LocalContext.current
                                val uri = if (isOffline) {
                                    try {
                                        val sanitizedUrl = item.url.replace("https:", "")

                                        // Use the stored relative path directly
                                        val file = File(context.filesDir, sanitizedUrl)
                                        // Use the stored relative path directly
                                        Log.d("ImageDebug", "Loading file: ${file.absolutePath}")

                                        if (file.exists()) {
                                            FileProvider.getUriForFile(
                                                context,
                                                "${context.packageName}.fileprovider",
                                                file
                                            ).toString()
                                        } else {
                                            Log.e("ImageDebug", "File not found: ${file.absolutePath}")
                                            null
                                        }
                                    } catch (e: Exception) {
                                        Log.e("ImageLoad", "Error: ${e.message}")
                                        null
                                    }
                                } else {
                                    item.url
                                }

                                // Display image or placeholder
                                if (uri != null) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(uri)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = item.caption,
                                        modifier = imgModifier
                                    )
                                } else {
                                    // Show error placeholder
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp)
                                            .background(Color.LightGray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Image unavailable offline")
                                    }
                                }
                                item.caption?.let { cap ->
                                    if (!isLikelyIcon) Text(
                                        text = cap,
                                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                                        color = Color.Gray,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(4.dp)
                                    )
                                }
                            }
                            is ContentItem.Gallery -> {
                                Spacer(Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    item.images.forEach { img ->
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(4.dp)
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(img.url)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = img.caption,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .width(100.dp)
                                                    .aspectRatio((img.widthAttr ?: 1).toFloat() / (img.heightAttr ?: 1))
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFFEEEEEE))
                                            )
                                            img.caption?.let { cap ->
                                                Text(
                                                    text = cap,
                                                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onBackground,
                                                    modifier = Modifier.padding(top = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            is ContentItem.Table -> {
                                Spacer(Modifier.height(16.dp))
                                val tableScroll = rememberScrollState()
                                val columnWidth = 120.dp
                                Box(
                                    Modifier
                                        .horizontalScroll(tableScroll)
                                        .border(1.dp, Color.DarkGray, RoundedCornerShape(4.dp))
                                ) {
                                    Column(modifier = Modifier.padding(4.dp)) {
                                        // Header
                                        Row(Modifier.background(Color(0xFFCCCCCC))) {
                                            item.headers.forEachIndexed { i, header ->
                                                Text(
                                                    text = header,
                                                    modifier = Modifier
                                                        .width(columnWidth)
                                                        .padding(8.dp),
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onBackground
                                                )
                                                if (i < item.headers.lastIndex) {
                                                    Spacer(
                                                        Modifier
                                                            .width(1.dp)
                                                            .fillMaxHeight()
                                                            .background(Color.DarkGray)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(Modifier
                                            .height(1.dp)
                                            .background(Color.DarkGray))
                                        // Rows
                                        item.rows.forEachIndexed { rowIndex, rowEls ->
                                            val bg = if (rowIndex % 2 == 0) Color(0xFFF7F7F7) else Color(0xFFEDEDED)
                                            Row(Modifier.background(bg)) {
                                                rowEls.forEachIndexed { ci, cellEl ->
                                                    Box(Modifier.width(columnWidth).padding(8.dp)) {
                                                        val imgTag = cellEl.selectFirst("img")
                                                        if (imgTag != null) {
                                                            val raw = imgTag.attr("src")
                                                            val src = when {
                                                                raw.startsWith("//") -> "https:$raw"
                                                                raw.startsWith("http") -> raw
                                                                else -> "https:$raw"
                                                            }
                                                            AsyncImage(
                                                                model = ImageRequest.Builder(LocalContext.current)
                                                                    .data(src)
                                                                    .crossfade(true)
                                                                    .build(),
                                                                contentDescription = imgTag.attr("alt"),
                                                                contentScale = ContentScale.Fit,
                                                                modifier = Modifier
                                                                    .height(80.dp)
                                                                    .clip(RoundedCornerShape(4.dp))
                                                            )
                                                        } else {
                                                            Text(
                                                                text = cellEl.text(),
                                                                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                                                                color = MaterialTheme.colorScheme.onBackground
                                                            )
                                                        }
                                                    }
                                                    if (ci < rowEls.lastIndex) {
                                                        Spacer(
                                                            Modifier
                                                                .width(1.dp)
                                                                .fillMaxHeight()
                                                                .background(Color.Gray)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = showTOC,
                    enter = fadeIn(tween(300)) + slideInHorizontally(tween(300)),
                    exit = fadeOut(tween(300)) + slideOutHorizontally(tween(300))
                ) {
                    Surface(
                        Modifier
                            .width(220.dp)
                            .fillMaxHeight()
                            .padding(8.dp),
                        tonalElevation = 4.dp,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                "Contents",
                                style = androidx.compose.material3.MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onBackground

                            )
                            Spacer(Modifier.height(8.dp))
                            tocEntries.forEach { (idx, title) ->
                                Text(
                                    text = title,
                                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .clickable {
                                            coroutine.launch {
                                                listState.animateScrollToItem(idx + 1)
                                            }
                                        }
                                        .padding(vertical = 4.dp)
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            if (showSaveDialog) {
                AlertDialog(
                    onDismissRequest = { showSaveDialog = false },
                    title = { Text("Saving Article") },
                    text = {
                        when (val state = saveState) {
                            is SaveState.Progress -> Column {
                                LinearProgressIndicator(
                                    progress = if (state.total > 0) state.current.toFloat() / state.total.toFloat() else 0f,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(8.dp))
                                Text("Saving resources (${state.current}/${state.total})")
                            }
                            is SaveState.Error -> Text("Error: ${state.message}")
                            is SaveState.Success -> Text("Article saved successfully!")
                            else -> CircularProgressIndicator()
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showSaveDialog = false }) {
                            Text("Dismiss")
                        }
                    }
                )
            }

            // Replace the existing IconButton code with:
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val density = LocalDensity.current
                var offsetX by remember { mutableStateOf(0f) }
                var offsetY by remember { mutableStateOf(0f) }

                LaunchedEffect(Unit) {
                    with(density) {
                        // Initial position: bottom-right (16.dp padding from edges)
                        offsetX = maxWidth.toPx() - 64.dp.toPx()  // 64.dp = button width (48.dp + 16.dp padding)
                        offsetY = maxHeight.toPx() - 64.dp.toPx() // 64.dp = button height (48.dp + 16.dp padding)
                    }
                }

                Box(
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                with(density) {
                                    offsetX = (offsetX + dragAmount.x).coerceIn(
                                        0f,
                                        maxWidth.toPx() - 64.dp.toPx()  // Keep within right edge
                                    )
                                    offsetY = (offsetY + dragAmount.y).coerceIn(
                                        0f,
                                        maxHeight.toPx() - 64.dp.toPx() // Keep within bottom edge
                                    )
                                }
                            }
                        }
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0x80FFFFFF))
                        .clickable { showTOC = !showTOC }
                        .padding(16.dp)
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Toggle TOC",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
