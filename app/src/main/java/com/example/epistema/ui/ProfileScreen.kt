package com.example.epistema.ui
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.IconButton
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.epistema.viewmodels.SearchViewModel
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import androidx.lifecycle.viewmodel.compose.viewModel

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
    viewModel: SearchViewModel = viewModel()
) {
    val article by viewModel.currentArticle.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showTOC by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutine = rememberCoroutineScope()

    val contentItems = remember(article?.content) {
        val html = article?.content.orEmpty()
        val doc = Jsoup.parse(html)
        val elems = doc.select("h2, h3, p, ul, ol, img, div.trow, table.wikitable")
        val raw = mutableListOf<ContentItem>()
        for (el in elems) {
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
                            rawSrc.startsWith("http")  -> rawSrc
                            else                        -> "https:$rawSrc"
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
                        && el.parents().none { it.tagName() == "table" && it.hasClass("wikitable") } -> {
                    val rawSrc = el.attr("src")
                    val url = when {
                        rawSrc.startsWith("//") -> "https:$rawSrc"
                        rawSrc.startsWith("http")  -> rawSrc
                        else                        -> "https:$rawSrc"
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
        }
        raw
    }

    val tocEntries = remember(contentItems) {
        contentItems.mapIndexedNotNull { idx, item ->
            (item as? ContentItem.Header)
                ?.takeIf { it.level == 2 }
                ?.let { idx to it.text }
        }
    }

    Surface(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator()
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
                                text = article?.title.orEmpty(),
                                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                Log.d(TAG, "TTS clicked for ‘${article?.title}’")
                            }) {
                                androidx.compose.material3.Icon(
                                    Icons.Default.VolumeUp,
                                    contentDescription = "Read Aloud"
                                )
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
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                        )
                                    else
                                        androidx.compose.material3.MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                                        ),
                                    modifier = Modifier.padding(vertical = 4.dp)
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
                                            } else append(node.text())
                                            else -> append(node.outerHtml())
                                        }
                                    }
                                }
                                ClickableText(
                                    text = annotated,
                                    modifier = Modifier.padding(vertical = 4.dp),
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
                                    onClick = { /* no-op */ }
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
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(item.url)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = item.caption,
                                    contentScale = contentScale,
                                    modifier = imgModifier
                                )
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
                                                    color = Color.Gray,
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
                                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                                )
                                                if (i < item.headers.lastIndex) {
                                                    Divider(
                                                        modifier = Modifier
                                                            .width(1.dp)
                                                            .fillMaxHeight()
                                                            .background(Color.DarkGray)
                                                    )
                                                }
                                            }
                                        }
                                        Divider(color = Color.DarkGray, thickness = 1.dp)
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
                                                                raw.startsWith("http")  -> raw
                                                                else                      -> "https:$raw"
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
                                                                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                                                            )
                                                        }
                                                    }
                                                    if (ci < rowEls.lastIndex) {
                                                        Divider(
                                                            modifier = Modifier
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
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
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

            IconButton(
                onClick = { showTOC = !showTOC },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color(0x80FFFFFF), RoundedCornerShape(4.dp))
                    .clip(RoundedCornerShape(4.dp))
            ) {
                androidx.compose.material3.Icon(Icons.Default.Menu, contentDescription = "Toggle TOC")
            }
        }
    }
}